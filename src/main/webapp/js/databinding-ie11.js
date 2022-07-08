"use strict";

// Proxy polyfill
(function(){function l(){function n(a){return a?"object"===typeof a||"function"===typeof a:!1}var p=null;var g=function(a,b){function f(){}if(!n(a)||!n(b))throw new TypeError("Cannot create proxy with a non-object as target or handler");p=function(){f=function(a){throw new TypeError("Cannot perform '"+a+"' on a proxy that has been revoked");}};var e=b;b={get:null,set:null,apply:null,construct:null};for(var k in e){/*if(!(k in b))throw new TypeError("Proxy polyfill does not support trap '"+k+"'");*/b[k]=e[k]}"function"===
typeof e&&(b.apply=e.apply.bind(e));var c=this,g=!1,q=!1;"function"===typeof a?(c=function(){var h=this&&this.constructor===c,d=Array.prototype.slice.call(arguments);f(h?"construct":"apply");return h&&b.construct?b.construct.call(this,a,d):!h&&b.apply?b.apply(a,this,d):h?(d.unshift(a),new (a.bind.apply(a,d))):a.apply(this,d)},g=!0):a instanceof Array&&(c=[],q=!0);var r=b.get?function(a){f("get");return b.get(this,a,c)}:function(a){f("get");return this[a]},v=b.set?function(a,d){f("set");b.set(this,
a,d,c)}:function(a,b){f("set");this[a]=b},t={};Object.getOwnPropertyNames(a).forEach(function(b){if(!((g||q)&&b in c)){var d={enumerable:!!Object.getOwnPropertyDescriptor(a,b).enumerable,get:r.bind(a,b),set:v.bind(a,b)};Object.defineProperty(c,b,d);t[b]=!0}});e=!0;Object.setPrototypeOf?Object.setPrototypeOf(c,Object.getPrototypeOf(a)):c.__proto__?c.__proto__=a.__proto__:e=!1;if(b.get||!e)for(var m in a)t[m]||Object.defineProperty(c,m,{get:r.bind(a,m)});Object.seal(a);Object.seal(c);return c};g.revocable=
function(a,b){return{proxy:new g(a,b),revoke:p}};return g};var u="undefined"!==typeof process&&"[object process]"==={}.toString.call(process)||"undefined"!==typeof navigator&&"ReactNative"===navigator.product?global:self;u.Proxy||(u.Proxy=l(),u.Proxy.revocable=u.Proxy.revocable);})();

// transpiled with https://babeljs.io/repl

function _defineProperty(obj, key, value) { if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }

// For trim() polyfill see https://jsperf.com/mega-trim-test/25

var trim16_whitespace = ' \n\r\t\v\f\u00a0\u2000\u2001\u2002\u2003\u2004\u2005\u2006\u2007\u2008\u2009\u200a\u200b\u2028\u2029\u3000';

function trim16(s) {
  var i = 0,
      j = s.length - 1;
  while (i < s.length && trim16_whitespace.indexOf(s.charAt(i)) != -1)
  i++;
  while (j > i && trim16_whitespace.indexOf(s.charAt(j)) != -1)
  j--;
  return s.substring(i, j + 1);
}

// See https://stackoverflow.com/a/54224557
function isProxy(o) {
  return o ? !!o["__proxy__"] : false;
}

function getTarget(o) {
  return isProxy(o) ? o["__target__"] : o;
}

function getParent(o) {
  return isProxy(o) ? o["__parent__"] : o;
}

function isArray(o) {
  return Array.isArray(o);
}

var gwtEnumClass = null; // if set may improve observe.isGwtEnum()

function observe(o, callback) {

  function buildProxy(prefix, o, parentProxy, callback_override) {
    if (isProxy(o)) {
      var cb = { prefix: prefix, callback: callback };
      if (callback_override) cb.callback = callback_override;
      o["__callback__"] = cb;
      return o;
    }

    o["__proxy__"] = null;
    o["__target__"] = null;
    o["__parent__"] = null;
    o["__callback__"] = null;

    var mapPrefix = "__internmap__"; // to safely use objects as maps for our data

    var newProxy = new Proxy(o, {

      nestedProxies: {},

      functionProxies: {},

      callbacks: [ { prefix: prefix, callback: callback } ],

      addCallback: function (cb) {
        var len = this.callbacks.length;
        for (var i = 0; i < len; i++) {
          if (this.callbacks[i].prefix === cb.prefix && this.callbacks[i].callback === cb.callback) return;
        }
        this.callbacks.push(cb);
      },

      execCallbacks: function (proxy, target, property, oldVal, newVal) {
        var len = this.callbacks.length;
        for (var i = 0; i < len; i++) {
          this.callbacks[i].callback(proxy, target, this.callbacks[i].prefix + property, oldVal, newVal);
        }
      },

      set: function set(target, property, value) {
        if (property === "__proxy__") throw new Error("You cannot set the value of '__proxy__'");
        if (property === "__target__") throw new Error("You cannot set the value of '__target__'");
        if (property === "__parent__") throw new Error("You cannot set the value of '__parent__'");

        if (property === "__callback__") {
          this.addCallback(value);
          return true;
        }

        var ownProxy = this.nestedProxies[mapPrefix + property];
        if (ownProxy && ownProxy === value) {
          var v0 = target[property];
          if (v0 === value) return true;
          var v1 = getTarget(value);
          if (v0 === v1) return true;
          // for example proxied array is the same, but its elements were changed
          target[property] = v1;
          this.execCallbacks(newProxy, target, property, v0, v1);
          return true;
        }
        ownProxy = this.functionProxies[mapPrefix + property];
        if (ownProxy && ownProxy === value) {
          var v0 = target[property];
          if (v0 === value) return true;
          var v1 = getTarget(value);
          if (v0 === v1) return true;
          target[property] = v1;
          this.execCallbacks(newProxy, target, property, v0, v1);
          return true;
        }

        var oldV = target[property];
        var newV = value;
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

      get: function get(target, property) {
        if (property === "__proxy__") return true;
        if (property === "__target__") return target;
        if (property === "__parent__") return parentProxy;
        if (property === "__callback__") return undefined;

        var out = target[property];
        if (!out) return out; // in JS typeof null is 'object', see https://2ality.com/2013/10/typeof-null.html

        // return a new proxy if possible, add to prefix
        if (typeof out === "object")  { // instanceof fails with GWT!
          var rslt = this.nestedProxies[mapPrefix + property];
          if (!rslt) {
            if (isNonProxyable(out)) return out; // no proxy for GWT enum, because its equals() is just ===
            if (parentProxy) { // circular reference detection
                var outTarget = getTarget(out);
                var p = parentProxy;
                while (p) {
                    var t = getTarget(p);
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
            var len = this.callbacks.length;
            for (var i = 1; i < len; i++) { // from index 1, because 0 is already processed
              buildProxy(this.callbacks[i].prefix + property + '.', rslt, newProxy, this.callbacks[i].callback);
            }
          }
          return rslt;
        } else if (typeof out === "function") { // https://2ality.com/2015/10/intercepting-method-calls.html
          var fnProxy = this.functionProxies[mapPrefix + property];
          if (!fnProxy || fnProxy.target !== out) {
            var wrap = function () {
              for (var _len = arguments.length, args = new Array(_len), _key = 0; _key < _len; _key++) {
                args[_key] = arguments[_key];
              }
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

      /*deleteProperty: function deleteProperty(target, property) {
        // not supported by IE11 polyfill
        if (property in target) {
          var tv = target[property];
          delete target[property];
          this.execCallbacks(newProxy, target, property, tv, undefined);
          return true;
        }
      }*/
    });

    o["__proxy__"] = undefined;
    o["__target__"] = undefined;
    o["__parent__"] = undefined;
    o["__callback__"] = undefined;
    return newProxy;
  }

  function isNonProxyable(obj) {
    if (!obj) return false;
    if (obj instanceof Element || obj instanceof HTMLDocument) return true; // https://stackoverflow.com/a/36894871
    var s = Object.prototype.toString.call(obj);
    if (s === "[object Map]" || s === "[object Date]" || s === "[object Set]") return true; // https://stackoverflow.com/a/44198641
    return isGwtEnum(obj);
  }

  var knownEnumProto = null;

  function isGwtEnum(obj) {
    if (!obj) return false;
    var cnt = Object.keys(obj).length;
    if (cnt < 2 || cnt > 40) return false; // enum instance

    var p1 = Object.getPrototypeOf(obj);
    if (p1 === null) return false;
    var p2 = Object.getPrototypeOf(p1);
    if (p2 === null) return false;
    if (knownEnumProto) {
      return p2 === knownEnumProto;
    }
    var keys2 = Object.keys(p2);
    cnt = keys2.length;
    if (cnt < 2 || cnt > 40) return false; // java.lang.Enum prototype

    for (var i = 0; i < cnt; i++) {
      var k = keys2[i];
      var v = p2[k];
      if (typeof v === "object") {
        if (gwtEnumClass) {
          if (v === gwtEnumClass) {
            knownEnumProto = p2;
            return true;
          } else {
            continue;
          }
        }
        var vkeys = Object.keys(v);
        var vlen = vkeys.length;
        if (vlen < 2 || cnt > 20) continue; // java.lang.Enum descriptor
        var java_lang = false;
        var java_enum = false;
        for (var j = 0; j < vlen; j++) {
          var vk = vkeys[j];
          var vv = v[vk];
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
  var p = field.indexOf('[');
  if (p >= 0) {
    var s = field.slice(p+1);
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
  var arr = chain.split('.');
  if (arr.length === 0) return undefined;
  if (arr.length === 1) {
    return defineField(obj, arr[0], value, forceSet);
  }
  var o = obj;
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
  if (typeof String.prototype.trim !== 'function') String.prototype.trim = trim16;

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
