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
					const newCard = { fileUri: uri, fileName: file.name, loading: true };
					const index = VUiPage.vueData.aiFileResponses.push(newCard) - 1;
										
					VUiPage.$http.post('_analyze', VUiPage.objectToFormData({ CTX: VUiPage.vueData.CTX, fileUri: uri }))
						.then(response => {
							VUiPage.vueData.aiFileResponses[index] = {...response.data, ...VUiPage.vueData.aiFileResponses[index]};
							VUiPage.vueData.aiFileResponses[index].loading = false;
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
						const newCard = { fileUri: uri, fileName: file.name, loading: true };
						VUiPage.vueData.aiOrdoResponse = newCard;
											
						VUiPage.$http.post('_analyzeOrdo', VUiPage.objectToFormData({ CTX: VUiPage.vueData.CTX, fileUri: uri, fileUris: VUiPage.vueData.aiQuery.docUris }))
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


	function getCardByUri(uri) {
		return VUiPage.vueData.aiFileResponses.find(card => card.fileUri === uri);
	}
	
	VUiPage.vueData.receivingCalls = [];
	
});

VUiExtensions.methods.analyzeTransportText = function(text) {
	const newCard = { loading: true, text: text };
	const index = VUiPage.vueData.aiResponses.push(newCard) - 1;
						
	VUiPage.$http.post('_analyze', VUiPage.objectToFormData({ CTX: VUiPage.vueData.CTX, text: text }))
		.then(response => {
			VUiPage.vueData.aiResponses[index] = {...response.data, ...VUiPage.vueData.aiResponses[index]};
			VUiPage.vueData.aiResponses[index].loading = false;
		})
		.catch(error => {
			const card = VUiPage.vueData.aiResponses[index];
			card.loading = false;
			card.error = error;
		});
}


const calls = [
`Agent : Bonjour, service technique à votre écoute, pouvez vous me donner votre numéro d'identification et une description de votre problème ?
Appelant : mon code est le liv 42 12 et je viens de crever sur l'A86 extérieure près de la sortie 30, je suis sur le bas côté en warning, vous pouvez m'envoyer un dépanneur ?
Agent : c'est noté, nous vous envoyons un dépanneur de suite.
Appelant : merci`,

`Agent : Bonjour, service technique à votre écoute, pouvez vous me donner votre numéro d'identification et une description de votre problème ?
Appelant : Bonjour, le panneaux de vitesse est taggé à créteil sur la D86 entre le croisement de la d201 et la station BP.
Agent : C'est noté merci, quel est vôtre matricule ?
Appelant : vl 221`,

`Agent : Bonjour, service technique à votre écoute, pouvez vous me donner votre numéro d'identification et une description de votre problème ?
Appelant : Bonjour, je suis le conducteur p l deux cent quarente deux et j'ai un problème avec mon clignotant droite
Agent : Merci, j'en informe le centre de maintenance`,

`Agent : Bonjour, service technique à votre écoute, pouvez vous me donner votre numéro d'identification et une description de votre problème ?
Appelant : Oui, bon comme d'hab c'est totalement bouché pour accéder à la 118, je vais avoir du retard pour la livraison
Agent : Merci du signalement, pouvez vous me dire où vous êtes et votre matricule s'il vous plait ?
Appelant : Oui, VL 512, je suis à boulogne
Agent : Merci, c'est noté !`,

`Agent : Bonjour, service technique à votre écoute, pouvez vous me donner votre numéro d'identification et une description de votre problème ?
Appelant : oui, c'est PL 52 13, le pont Général-Audibert est fermé, je prend la déviation par le pont de la Haudaudine.`,

`Agent : Bonjour, service technique à votre écoute, pouvez vous me donner votre numéro d'identification et une description de votre problème ?
Appelant : Bonjour, matricule pl 12 12, c'est pour vous signaler un vehicule arrêté sur le bas côté, périphérique sortie porte de la chapelle.
Agent : Merci, c'est noté !`,
];

/*
Bonjour, mon matricule est le VL_42 et je viens de tomber en panne au niveau du 18 avenue de l'opéra. Pouvez-vous envoyer une dépanneuse ?'
*/

VUiExtensions.methods.connectToCallCenter = function() {
	for (const call of calls) {
		// simulate calls by appening with random delay
		setTimeout(() => appendCall(call), Math.random() * 20000);
	}
}

function appendCall(text) {
	VUiPage.vueData.clicked=false;
	var words = text.split(' ');
	let receivingText  = { text: ''};
	VUiPage.vueData.receivingCalls.push(receivingText);
	setTimeout(() => addWord(receivingText, words, 0), Math.random() * 200 + 150);
}

function addWord(obj, texts, index) {
	VUiPage.vueData.receivingCalls[VUiPage.vueData.receivingCalls.indexOf(obj)].text = obj.text + ' ' + texts[index];
	if (index < texts.length - 1) {
		setTimeout(() => addWord(obj, texts, index + 1), Math.random() * 200 + 150);
	} else {
		setTimeout(() => {
			VUiExtensions.methods.analyzeTransportText (obj.text.substring(obj.text.indexOf("\n") + 1));
			VUiPage.vueData.receivingCalls.splice(VUiPage.vueData.receivingCalls.indexOf(obj), 1);
			}, 2000);
	}
}

