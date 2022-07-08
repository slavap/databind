// See https://stackoverflow.com/a/54224557
function isProxy(o) {
  return o ? !!o[Symbol.for("__proxy__")] : false;
}

function getTarget(o) {
  return isProxy(o) ? o[Symbol.for("__target__")] : o;
}

function getParent(o) {
  return isProxy(o) ? o[Symbol.for("__parent__")] : o;
}

function isArray(o) {
  return Array.isArray(o);
}

let gwtEnumClass = null; // if set may improve observe.isGwtEnum()

function observe(o, callback) {

  function buildProxy(prefix, o, parentProxy, callback_override) {
    if (isProxy(o)) {
      let cb = { prefix: prefix, callback: callback };
      if (callback_override) cb.callback = callback_override;
      o[Symbol.for("__callback__")] = cb;
      return o;
    }

    const mapPrefix = "__internmap__"; // to safely use objects as maps for our data

    let newProxy = new Proxy(o, {

      nestedProxies: {},

      functionProxies: {},

      callbacks: [ { prefix: prefix, callback: callback } ],

      addCallback: function (cb) {
        let idx = this.callbacks.findIndex(elt => elt.prefix === cb.prefix && elt.callback === cb.callback);
        if (idx === -1) this.callbacks.push(cb);
      },

      execCallbacks: function (proxy, target, property, oldVal, newVal) {
        this.callbacks.forEach(elt => elt.callback(proxy, target, elt.prefix + property, oldVal, newVal));
      },

      set(target, property, value) {
        if (property === Symbol.for("__proxy__" )) throw new Error("You cannot set the value of '__proxy__'");
        if (property === Symbol.for("__target__")) throw new Error("You cannot set the value of '__target__'");
        if (property === Symbol.for("__parent__")) throw new Error("You cannot set the value of '__parent__'");

        if (property === Symbol.for("__callback__")) {
          this.addCallback(value);
          return true;
        }

        let ownProxy = this.nestedProxies[mapPrefix + property];
        if (ownProxy && ownProxy === value) {
          let v0 = target[property];
          if (v0 === value) return true;
          let v1 = getTarget(value);
          if (v0 === v1) return true;
          // for example proxied array is the same, but its elements were changed
          target[property] = v1;
          this.execCallbacks(newProxy, target, property, v0, v1);
          return true;
        }
        ownProxy = this.functionProxies[mapPrefix + property];
        if (ownProxy && ownProxy === value) {
          let v0 = target[property];
          if (v0 === value) return true;
          let v1 = getTarget(value);
          if (v0 === v1) return true;
          target[property] = v1;
          this.execCallbacks(newProxy, target, property, v0, v1);
          return true;
        }

        let oldV = target[property];
        let newV = value;
        if (oldV !== newV) {
          if (typeof oldV === "object")  { // instanceof fails with GWT!
            this.nestedProxies[mapPrefix + property] = undefined;
          } else if (typeof oldV === "function") {
            this.functionProxies[mapPrefix + property] = undefined;
          }
          target[property] = newV;
          this.execCallbacks(newProxy, target, property, oldV, newV);
        }
        return true; // See https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Proxy/handler/set#Invariants
      },

      get(target, property) {
        if (property === Symbol.for("__proxy__")) return true;
        if (property === Symbol.for("__target__")) return target;
        if (property === Symbol.for("__parent__")) return parentProxy;
        if (property === Symbol.for("__callback__")) return undefined;

        const out = target[property];
        if (!out) return out; // in JS typeof null is 'object', see https://2ality.com/2013/10/typeof-null.html

        // return a new proxy if possible, add to prefix
        if (typeof out === "object")  { // instanceof fails with GWT!
          let rslt = this.nestedProxies[mapPrefix + property];
          if (!rslt) {
            if (isNonProxyable(out)) return out; // no proxy for GWT enum, because its equals() is just ===
            if (parentProxy) { // circular reference detection
                let outTarget = getTarget(out);
                let p = parentProxy;
                while (p) {
                    let t = getTarget(p);
                    if (outTarget === t) {
                        return out; // no need to proxy
                    }
                    p = getParent(p);
                }
            }
          }
          if (rslt && getTarget(rslt) !== out) {
            rslt = null;
            this.nestedProxies[mapPrefix + property] = undefined;
          }
          if (!rslt) {
            if (out) {
              rslt = buildProxy(prefix + property + '.', out, newProxy);
              this.nestedProxies[mapPrefix + property] = rslt; // to prevent multiple proxy creation
            } else {
              rslt = out;
            }
          } else {
            rslt = buildProxy(prefix + property + '.', rslt, newProxy);
          }
          if (rslt) {
            let len = this.callbacks.length;
            for (let i = 1; i < len; i++) { // from index 1, because 0 is already processed
              buildProxy(this.callbacks[i].prefix + property + '.', rslt, newProxy, this.callbacks[i].callback);
            }
          }
          return rslt;
        } else if (typeof out === "function") { // https://2ality.com/2015/10/intercepting-method-calls.html
          let fnProxy = this.functionProxies[mapPrefix + property];
          if (!fnProxy || fnProxy.target !== out) {
            let wrap = function (...args) {
              try {
                return out.apply(this, args);
              } catch (err) {
                return out.apply(target, args); // some classes work with target only (Map, Date, HTMLElement, ...)
              }
            };
            fnProxy = { target: out, wrap: wrap };
            this.functionProxies[mapPrefix + property] = fnProxy;
          }
          return fnProxy.wrap;
        }
        return out; // primitive, just ignore
      },

      deleteProperty(target, property) { // not supported by IE11 polyfill
        if (property in target) {
          let tv = target[property];
          delete target[property];
          this.execCallbacks(newProxy, target, property, tv, undefined);
          return true;
        }
      }
    });
    return newProxy;
  }

  function isNonProxyable(obj) {
    if (!obj) return false;
    if (obj instanceof Element || obj instanceof HTMLDocument) return true; // https://stackoverflow.com/a/36894871
    let s = Object.prototype.toString.call(obj);
    if (s === "[object Map]" || s === "[object Date]" || s === "[object Set]") return true; // https://stackoverflow.com/a/44198641
    return isGwtEnum(obj);
  }

  let knownEnumProto = null;

  function isGwtEnum(obj) {
    if (!obj) return false;
    let cnt = Object.keys(obj).length;
    if (cnt < 2 || cnt > 40) return false; // enum instance
    let p1 = Object.getPrototypeOf(obj);
    if (p1 === null) return false;
    let p2 = Object.getPrototypeOf(p1);
    if (p2 === null) return false;
    if (knownEnumProto) {
      return p2 === knownEnumProto;
    }
    let keys2 = Object.keys(p2);
    cnt = keys2.length;
    if (cnt < 2 || cnt > 40) return false; // java.lang.Enum prototype
    for (let i = 0; i < cnt; i++) {
      let k = keys2[i];
      let v = p2[k];
      if (typeof v === "object") {
        if (gwtEnumClass) {
          if (v === gwtEnumClass) {
            knownEnumProto = p2;
            return true;
          } else {
            continue;
          }
        }
        let vkeys = Object.keys(v);
        let vlen = vkeys.length;
        if (vlen < 2 || cnt > 20) continue; // java.lang.Enum descriptor
        let java_lang = false;
        let java_enum = false;
        for (let j = 0; j < vlen; j++) {
          let vk = vkeys[j];
          let vv = v[vk];
          if (typeof vv === "string") {
            if (!java_lang && "java.lang" === vv) {
              java_lang = true;
              if (java_enum) {
                knownEnumProto = p2;
                return true;
              }
              else continue;
            }
            if (!java_enum && "Enum" === vv) {
              java_enum = true;
              if (java_lang) {
                knownEnumProto = p2;
                return true;
              }
              else continue;
            }
            if (vv.startsWith("java.")) return false;
          }
        }
      }
    }
    return false;
  }

  return o ? buildProxy('', o, null/*newProxy*/) : o;
}

/** @param forceSet - if true value will be always set (even if field is already existed).
 *  @returns current value of field.
*/
function defineField(obj, field, value, forceSet) {
  if (!obj || field === '*' || field === '**') return undefined;
  let p = field.indexOf('[');
  if (p >= 0) {
    let s = field.slice(p+1);
    field = field.slice(0, p).trim();
    p = s.indexOf(']');
    s = s.slice(0, p).trim();
    if (!isArray(obj[field])) obj[field] = [];
    if (forceSet || obj[field][s] === undefined) obj[field][s] = (typeof value !== "undefined") ? value : null;
    return obj[field][s];
  }
  if (typeof value === 'object' && typeof obj[field] !== 'object') obj[field] = value;
  else if (forceSet || obj[field] === undefined) obj[field] = (typeof value !== "undefined") ? value : null;
  return obj[field];
}

/** @param chain - like a.b.c or a.b[3].c, also a.b.* and a.b.** are supported.
 *  @param value - optional, value to set in case field does not exist (null will be used by default).
 *  @param forceSet - if true value will be always set (even if field is already existed).
 *  @returns current value of field chain.
 */
function defineFieldChain(obj, chain, value, forceSet) {
  if (!obj) return undefined;
  let arr = chain.split('.');
  if (arr.length === 0) return undefined;
  if (arr.length === 1) {
    return defineField(obj, arr[0], value, forceSet);
  }
  let o = obj;
  for (var i = 0; i < arr.length-1; i++) {
    o = defineField(o, arr[i], {}); // forceSet should not be passed here, it's intermidiate chain member
  }
  return defineField(o, arr[arr.length-1], value, forceSet);
}

/** @returns current value of field. */
function getFieldValue(obj, field) {
  if (!obj || field === '*' || field === '**') return undefined;
  let p = field.indexOf('[');
  if (p >= 0) {
    let s = field.slice(p+1);
    field = field.slice(0, p).trim();
    p = s.indexOf(']');
    s = s.slice(0, p).trim();
    if (!isArray(obj[field]) || obj[field][s] === undefined) return undefined;
    return obj[field][s];
  }
  return obj[field];
}

/** @param chain - like a.b.c or a.b[3].c, also a.b.* and a.b.** are supported.
 *  @returns current value of field chain (no any intermediate fields are created in opposite to defineFieldChain() method).
 */
function getFieldChainValue(obj, chain) {
  if (!obj) return undefined;
  let arr = chain.split('.');
  if (arr.length === 0) return undefined;
  if (arr.length === 1) {
    return getFieldValue(obj, arr[0]);
  }
  let o = obj;
  for (var i = 0; i < arr.length-1; i++) {
    o = getFieldValue(o, arr[i]);
    if (!o) return o;
  }
  return getFieldValue(o, arr[arr.length-1]);
}

(function observeInit() {
  document.swdatabinder = {
    observe: observe,
    isProxy: isProxy,
    getTarget: getTarget,
    isArray: isArray,
    defineFieldChain: defineFieldChain,
    getFieldChainValue: getFieldChainValue,
    setGwtEnumClass: function (value) {
      gwtEnumClass = value;
    }
  }
}());
