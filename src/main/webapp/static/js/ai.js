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
							card.error = error.response.data.globalErrors.join(' ');
							VUiPage.uiMessageStack = {}; // clear error messages stack, keep only flash message
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
	
	VUiPage.$watch('vueData.aiDocument.docUris',
		(newValue, oldValue) => {
			if (newValue.length === 0) return;
			
			// we read from component to have file names without asking serverside
			const files = VUiPage.$refs.aiFileUpload.files;
			
			// do analyze if needed
			for (const file of files) {
				const uri = file.fileUri;
				const isNew = !VUiPage.componentStates.addFiles.find(f => f.fileUri === uri);
				if (isNew) {
					console.log("Indexing " + uri);
					const newFile = { fileUri: uri, fileName: file.name, loading: true };
					const index = VUiPage.componentStates.addFiles.push(newFile) - 1;
										
					VUiPage.$http.post('_index', VUiPage.objectToFormData({ CTX: VUiPage.vueData.CTX, fileUri: uri }))
						.then(response => {
							VUiPage.componentStates.addFiles[index] = {...response.data, ...VUiPage.componentStates.addFiles[index]};
							VUiPage.componentStates.addFiles[index].loading = false;
							
							if (files.length === VUiPage.componentStates.addFiles.filter(f => f.loading === false).length) {
								// all files indexed
								VUiPage.httpPostAjax('_refresh', {}, {
									onSuccess : function() {
										setTimeout(() => VUiPage.$refs.addFiles.hide(), 1000);
									}
								});
							}
						})
						.catch(error => {
							const file = VUiPage.componentStates.addFiles[index];
							file.loading = false;
							file.error = error.response.data.globalErrors.join(' ');
							VUiPage.uiMessageStack = {}; // clear error messages stack, keep only flash message
						});

				}
			}
			
		});
		
	VUiPage.vueData.receivingCalls = [];
	VUiPage.vueData.chats=[
		{ messages: [], persona: { code: 'STE', name: 'Stéphane', description: "Assistant", avatar: 'https://cdn.quasar.dev/img/avatar1.jpg', welcome: "Bonjour, je m'appelle Stéphane, votre assistant virtuel, comment puis-je vous aider ?" }},
		{ messages: [], persona: { code: 'MAR', name: 'Marie', description: "Comptable", avatar: 'https://cdn.quasar.dev/img/avatar2.jpg', welcome: "Bonjour, je m'appelle Marie, chef comptable, comment puis-je vous aider ?" }},
		{ messages: [], persona: { code: 'DAN', name: 'Daniel', description: "Directeur de projets", avatar: 'https://cdn.quasar.dev/img/avatar4.jpg', welcome: "Bonjour, je m'appelle Daniel, directeur de projets, je vous écoute." }},
		{ messages: [], persona: { code: "ISA", name: "Isabelle", description: "Architecte Informatique", avatar: 'https://cdn.quasar.dev/img/avatar6.jpg', welcome: "Bonjour, je m'appelle  Isabelle, architecte en informatique, comment puis-je vous aider ?" }},
		{ messages: [], persona: { code: 'JUL', name: 'Julia', description: "Laconique", avatar: 'https://cdn.quasar.dev/img/avatar3.jpg', welcome: "Bonjour, je m'appelle  Julia, quel est votre question ?" }},
	];
	VUiPage.vueData.chatStreaming = true;
	VUiPage.vueData.tab = VUiPage.vueData.chats[0].persona.code;
	
});

VUiExtensions.methods.scrollToBottom = function(scroller) {
	VUiPage.$nextTick(() => {
		const scrollHeight = scroller.$el.children[0].children[0].scrollHeight; // workaround
		scroller.setScrollPosition('vertical',scrollHeight, 400);
		//scroller.setScrollPercentage('vertical',1);
	});
}

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
	setTimeout(() => addWord(receivingText, words, 0), words[0].length * 8 + 150);
}

function addWord(obj, texts, index) {
	VUiPage.vueData.receivingCalls[VUiPage.vueData.receivingCalls.indexOf(obj)].text = obj.text + ' ' + texts[index];
	if (index < texts.length - 1) {
		setTimeout(() => addWord(obj, texts, index + 1), texts[index + 1].length * 8 + 150);
	} else {
		setTimeout(() => {
			VUiExtensions.methods.analyzeTransportText (obj.text.substring(obj.text.indexOf("\n") + 1));
			VUiPage.vueData.receivingCalls.splice(VUiPage.vueData.receivingCalls.indexOf(obj), 1);
			}, 2000);
	}
}

VUiExtensions.methods.documentSearch = function() {
	if (this.vueData.aiDocument.prompt === '') return;
	
	const maxChunks = this.vueData.aiAdmin ? this.vueData.aiDocument.maxChunks || null : null;
	const minScore = this.vueData.aiAdmin ? this.vueData.aiDocument.minScore/100 || null: null;
	
	this.httpPostAjax('_search', {'query': this.vueData.aiDocument.prompt, 'maxChunks': maxChunks, 'minScore': minScore});
}

VUiExtensions.methods.resetChats = function() {
	for (const chat of this.vueData.chats) {
		chat.messages = [];
		chat.id = null;
		chat.chatting = false;
		chat.chattingDisplay = false;
	}
}

VUiExtensions.methods.chat = function(chatIdx, text, files, resultFn) {
	let chat = VUiPage.vueData.chats[chatIdx];
	chat.chatting = true;
	chat.chattingDisplay = true;
	chat.messages.push({text:text, type:'user'});
	VUiExtensions.methods.scrollToBottom(VUiPage.$refs.scroller[0]);
	
	// init chat if needed
	if (chat.id == null) {
		VUiPage.$http.post('_initChat', VUiPage.objectToFormData({ CTX: VUiPage.vueData.CTX, fileUris: files, persona: chat.persona.code }))
			.then(response => {
					chat.id = response.data.id;
					if (VUiPage.vueData.chatStreaming) {
						doChatStream(chat, text, resultFn);
					} else {
						doChat(chat, text, resultFn);
				    }
				})
			.catch(error => {
				console.log("error : " + error);
				// nope, only for demo
			});
	} else {
		if (VUiPage.vueData.chatStreaming) {
			doChatStream(chat, text, resultFn);
		} else {
			doChat(chat, text, resultFn);
		}
	}
		
}

function doChat(chat, text, resultFn) {
	// send text
	VUiPage.$http.post('_ask', VUiPage.objectToFormData({ CTX: VUiPage.vueData.CTX, prompt: text, chatId: chat.id }))
			.then(response => {
					chat.chatting = false;
					chat.chattingDisplay = false;
					chat.messages.push({text:response.data, type:'system'});
					resultFn(response.data);
					VUiExtensions.methods.scrollToBottom(VUiPage.$refs.scroller[0]);
				})
			.catch(error => {
				chat.chatting = false;
				chat.chattingDisplay = false;
				console.log("error : " + error);
				// nope, only for demo
			});
}


function doChatStream(chat, text, resultFn) {
	const evtSource = new EventSource("_askStream?" + new URLSearchParams({prompt: text, chatId: chat.id }).toString(), {withCredentials:true});
	evtSource.onopen = function(event) {
		chat.messages.push({text:"", type:'system'});		
	}
	evtSource.onmessage = function(event) {
		chat.chattingDisplay = false;
		const data = JSON.parse(event.data);
		chat.messages[chat.messages.length-1].text = data.message;
		VUiExtensions.methods.scrollToBottom(VUiPage.$refs.scroller[0]);
		if (data.end) {
			chat.chatting = false;
			chat.messages[chat.messages.length-1].sources = data.sources;
			resultFn(data.message);
			evtSource.close();
		}
	}
	evtSource.onerror = function(event) {
		console.log("EventSource error", event);
		chat.chatting = false;
		evtSource.close();
	}
}

// AI Speech to text
let recognition;

VUiExtensions.methods.isSpeechSupported= function() {
	return 'webkitSpeechRecognition' in window || 'SpeechRecognition' in window;
}
function getNewSpeechRecognition() {
	if ('SpeechRecognition' in window) {
		return new SpeechRecognition();
	} else if ('webkitSpeechRecognition' in window) {
		return new webkitSpeechRecognition();
	}
	return null;
}

VUiExtensions.methods.startSpeech = function(textFn, resultFn) {
	if (!this.isSpeechSupported()) return;
	
	recognition = getNewSpeechRecognition();
	recognition.continuous = false; // stop when user stop talking
	recognition.interimResults = true; // display interim results
	recognition.lang = 'fr-FR'; // demo is in french
	
	recognition.onresult = function(event) {
		let transcript = '';
		for (let i = event.resultIndex; i < event.results.length; i++) {
			transcript += event.results[i][0].transcript;
		}
		textFn(transcript);
	}
	
	recognition.onspeechend = function() {
		recognition.stop();
		resultFn();
	}
	
	recognition.start();
}

VUiExtensions.methods.stopSpeech = function() {
	if (!this.isSpeechSupported()) return;
	recognition.stop();
}
	
VUiExtensions.methods.equipmentAiSearch = function() {
	this.httpPostAjax('_searchAi', {'search': this.vueData.aiSearch}, {onSuccess: () => this.$refs.aiSearch.hide()});	
}

// detect pseudo KonamiCode (only arrows) to enable features (like streaming checkbox in chat)
let lastKeys = [];
document.addEventListener('keyup', (e) => {
	lastKeys.push(e.key);
	if (lastKeys.length > 8) {
		lastKeys.shift();
	}
	if (lastKeys.join('') === 'ArrowUpArrowUpArrowDownArrowDownArrowLeftArrowRightArrowLeftArrowRight') {
		VUiPage.vueData.aiAdmin = !VUiPage.vueData.aiAdmin;
		console.log("AI Admin mode : " + VUiPage.vueData.aiAdmin);
	}
});