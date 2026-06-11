VUiExtensions = {
	dataX : {},
	methods : {
		notificationTypes : function() {
			return {
				'MARS-LOGIN' : 'mdi-account-check',
				'MARS-LOGIN-ATTEMP' : 'mdi-account-alert',
				'MARS-BASE' : 'fas fa-dice-d20'
			}
		},        
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