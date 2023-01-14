var Pong = (function() {
	"use strict";
	var _parentDomain = undefined;
	var register = function (parent) {
		_parentDomain = parent;
		window.addEventListener("message", _handleMessage, _parentDomain);
	}
	function _handleMessage(evt) {
		if (evt.data.type == "ping") { // ping이 들어오면
			window.parent.postMessage({
				type : "pong",
				data : {}
			}, _parentDomain);
			
			_removeListenerAfterResponse();
		}
	}
	function _removeListenerAfterResponse() {
		window.removeEventListener("message", _handleMessage, _parentDomain);
	}

	return {
		register : register
	};
});
