VUiExtensions = {
	dataX : {},
	methods : {
		notificationTypes : function() {
			return {
				'MARS-LOGIN' : 'mdi-account-check',
				'MARS-LOGIN-ATTEMP' : 'mdi-account-alert',
				'MARS-TICKET-LEDGER' : 'mdi-mdi-issue mdi-ethereum',
				'MARS-MISSION-LEDGER' : 'mdi-mdi-school mdi-ethereum',
				'MARS-BASE' : 'fas fa-dice-d20'
			}
		},        
        getDocHeight : function(doc) {
            doc = doc || document;
            // stackoverflow.com/questions/...
            var body = doc.body, html = doc.documentElement;
            var height = Math.max( body.scrollHeight, body.offsetHeight, 
            html.clientHeight, html.scrollHeight, html.offsetHeight );
            return height;
        },
        setIframeHeight : function(ifrm) {
            var doc = ifrm.contentDocument? ifrm.contentDocument: 
            ifrm.contentWindow.document;
            ifrm.style.visibility = 'hidden';
            ifrm.style.height = "100px"; // reset to minimal height ...
            // IE opt. for bing/msn needs a bit added or scrollbar appears
            ifrm.style.height = getDocHeight( doc ) + 4 + "px";
            ifrm.style.visibility = 'visible';
        }
	}
}

quasarConfig = {
    loadingBar: { 'skip-hijack' : true } // disable quasar's ajaxbar
}
/*
if(typeof VueShortkey !== 'undefined') {
	Vue.use(VueShortkey);
}
*/