console.log("starting AI");


window.addEventListener('vui-after-page-mounted', function(event) {
    VUiPage.$watch('vueData.aiQuery.docUris',
        (newValue, oldValue) => {
			// we read from component to have file names without asking serverside
			const files = VUiPage.$refs.aiFileUpload.files;
			
			// do analyze if needed
			for (const file of files) {
				const uri = file.fileUri;
				if (!getCardByUri(uri)) {
					console.log("Analyzing " + uri);
					const newCard = { fileUri: uri, fileName: file.fileName, loading: true };
					const index = VUiPage.vueData.aiFileResponses.push(newCard) - 1;
										
					VUiPage.$http.post('_analyze', VUiPage.objectToFormData({ CTX: VUiPage.vueData.CTX, fileUri: uri }))
						.then(response => {
							VUiPage.vueData.aiFileResponses[index] = {...response.data, ...VUiPage.vueData.aiFileResponses[index]};
							VUiPage.vueData.aiFileResponses[index].loading = false;
							if (index == VUiPage.vueData.aiFileResponses.length - 1) {
								VUiPage.vueData.CTX = response.data.ctx;
							}
						})
						.catch(error => {
							const card = VUiPage.vueData.aiFileResponses[index];
							card.loading = false;
							card.error = error;
						});

				}
			}
			
			// remove, removed
			for (const card of VUiPage.vueData.aiFileResponses) {
				if (!files.find(file => file.fileUri === card.fileUri)) {
					console.log("Removing " + card.fileUri);
					VUiPage.vueData.aiFileResponses.splice(VUiPage.vueData.aiFileResponses.indexOf(card), 1);
				}
			}
        });
		
		VUiPage.$watch('vueData.aiQuery.docUri',
		        (newValue, oldValue) => {
					// we read from component to have file names without asking serverside
					const files = VUiPage.$refs.aiFileUploadOrdo.files;
					
					// do analyze if needed
					for (const file of files) {
						const uri = file.fileUri;
						console.log("Analyzing " + uri);
						const newCard = { fileUri: uri, fileName: file.fileName, loading: true };
						VUiPage.vueData.aiOrdoResponse = newCard;
											
						VUiPage.$http.post('_analyzeOrdo', VUiPage.objectToFormData({ CTX: VUiPage.vueData.CTX, fileUri: uri }))
							.then(response => {
								VUiPage.vueData.aiOrdoResponse = {...response.data, ...VUiPage.vueData.aiOrdoResponse};
								VUiPage.vueData.aiOrdoResponse.loading = false;
							})
							.catch(error => {
								const card = VUiPage.vueData.aiFileResponses[index];
								card.loading = false;
								card.error = error;
							});

					}
					
					// remove, removed
					for (const card of VUiPage.vueData.aiFileResponses) {
						if (!files.find(file => file.fileUri === card.fileUri)) {
							console.log("Removing " + card.fileUri);
							VUiPage.vueData.aiFileResponses.splice(VUiPage.vueData.aiFileResponses.indexOf(card), 1);
						}
					}
		        });
});


function getCardByUri(uri) {
	return VUiPage.vueData.aiFileResponses.find(card => card.fileUri === uri);
}