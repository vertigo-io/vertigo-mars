let Chatbot = {};
document.addEventListener('DOMContentLoaded', function () {

  const tour = new Shepherd.Tour({
    defaultStepOptions: {
      cancelIcon: {
        enabled: true
      },
      highlightClass: 'shepherd-highlight',
      scrollTo: { behavior: 'smooth', block: 'center' },
      when: {
        show: function() {
          sessionStorage.currentWelcomeTourStep = tour.getCurrentStep().id;
        },
        cancel: function () {
          sessionStorage.removeItem("currentWelcomeTourStep");
        }
      }
    }
  });

  tour.addStep({
    id: 'bases',
    title: 'Accéder à la liste des bases',
    text: `Pour commencer, cliquez sur l'onglet "Bases" pour accéder à la liste de toutes les bases du centre.`,
    attachTo: {
      element:  "a[href$='/basemanagement/bases/']",
      on: 'auto'
    },
    advanceOn: {
      selector: "a[href$='/basemanagement/bases/']",
      event: 'click'
    }
  });

  tour.addStep({
    id: 'baseDetails',
    title: 'Consulter les informations d\'une base',
    text: `Cliquez ensuite sur une des vignettes de la liste des bases pour affiche le détail de celle-ci.`,
    attachTo: {
      element: '.q-card',
      on: 'auto'
    },
    advanceOn: {
      selector: ".q-card",
      event: 'click'
    }
  });

  tour.addStep({
    id: 'addEquipment',
    title: 'Ajouter un équipement à la base',
    text: `Ce bouton permet d'ajouter un équipement à votre base. Cliquez-dessus pour procéder à l'ajout.`,
    attachTo: {
      element: "a[href$='/basemanagement/equipment/new']",
      on: 'auto'
    },
    advanceOn: {
      selector: "a[href$='/basemanagement/equipment/new']",
      event: 'click'
    }
  });

  tour.addStep({
    id: 'equipmentData',
    title: 'Renseigner les données de l\'équipement',
    text: `Avant d'ajouter votre équipement, il est nécessaire de renseigner les informations présentes dans ces blocs.`,
    attachTo: {
      element: "div[id='general']",
      on: 'auto'
    },
    buttons: [
      {
        action() {
          return this.next();
        },
        text: 'Suivant'
      }
    ],
  });

  tour.addStep({
    id: 'equipmentDataCreation',
    title: 'Ajouter l\'équipement',
    text: `Une fois les champs complétés, cliquez sur ce bouton pour procéder à l'ajout de l'équipement !`,
    attachTo: {
      element: "button[title='Create']",
      on: 'auto'
    },
    advanceOn: {
      selector: "button[title='Create']",
      event: 'click'
    }
  });

  tour.addStep({
    id: 'end',
    title: 'Fin',
    text: 'Fin !',
    attachTo: {
      element: "button[title='Create']",
      on: 'auto'
    },
    when: {
      show: function() {
        tour.complete();
        sessionStorage.removeItem("currentWelcomeTourStep");
      }
    }
  });

  if (sessionStorage.currentWelcomeTourStep !== null && sessionStorage.currentWelcomeTourStep !== undefined){
    tour.show(sessionStorage.currentWelcomeTourStep, true);
  }

    if (!String.prototype.startsWith) {
      String.prototype.startsWith = function (searchString, position) {
        position = position || 0;
        return this.substr(position, searchString.length) === searchString;
      };
    }

    function _chatbotWrapper() {

      let _initParam = null;
      let _button = null;
      let _iframe = null;

      function _createFlottingButton() {
        _button = document.createElement('div');

        _button.className = 'floating-button';

        _button.addEventListener('click', Chatbot.show);
        _button.addEventListener('mouseover', _buttonOver);
        _button.addEventListener('mouseout', _buttonOut);

        const img = document.createElement('img');
        img.className = 'avatar-img';
        img.src = _initParam.avatarUrl;
        img.alt = _initParam.botName;
        _button.appendChild(img);

        const text = document.createElement('div');
        text.className = 'avatar-text';
        text.textContent = _initParam.botName;
        _button.appendChild(text);

        document.body.appendChild(_button);
      }

      function addImageViewerModal() {
        const modal = document.createElement('div');
        modal.id = 'imageViewerModal';
        modal.className = 'modalChatbot';

        const spanClose = document.createElement('span');
        spanClose.id = 'close';
        spanClose.className = 'close';
        spanClose.innerHTML = '&times;';
        spanClose.addEventListener('click', hidePictureModal);

        const img = document.createElement('img');
        img.id = 'imgToView';
        img.className = 'modal-content-chatbot';
        modal.appendChild(spanClose);
        modal.appendChild(img);

        document.body.prepend(modal);
      }

      function hidePictureModal() {
        document.getElementById('imageViewerModal').style.display = 'none';
      }

      function _buttonOver() {
        _button.style.right = '0px';
      }

      function _buttonOut() {
        _button.style.right = '-90px';
      }

      function _createIframe() {
        _iframe = document.createElement('iframe');

        _iframe.className = 'iframe';
        _iframe.src = `${_initParam.botIHMBaseUrl}?runnerUrl=${_initParam.runnerUrl}&botName=${_initParam.botName}&useRating=${_initParam.useRating}`;

        if (_initParam.optionalParameters) {
          _initParam.optionalParameters.forEach(function (optionalParam) {
            _iframe.src += '&' + optionalParam.key + '=' + optionalParam.value;
          });
        }
        _iframe.style.visibility = 'hidden';

        document.body.appendChild(_iframe);
      }

      function checkIfConversationAlreadyExists() {
        _iframe.contentWindow.postMessage('conversationExist', '*');
      }

      function _initIframeListener() {
        window.addEventListener(
          'message',
          function (event) {
            if (event.data === 'Chatbot.minimize') {
              Chatbot.minimize();
            }
            else if (event.data === 'Chatbot.close') {
              Chatbot.minimize();
            }
            else if (event.data.conversationExist !== undefined) {
              if (event.data.conversationExist) {
                Chatbot.show();
              }
            }
            else if (event.data.context) {
              const map = {};
              event.data.context.forEach(function (value, key) {
                  if ( key === 'url' && value === '' ) {
                    map[key] = window.location.href;
                  } else {
                    const element = document.evaluate(value, document, null, XPathResult.ANY_TYPE, null);
                    const node = element.iterateNext();
                    if (node !== null) {
                      let elementValue;
                      if (node.attributes['value']) {
                        elementValue = node.attributes['value'].value;
                      } else {
                        elementValue = node.innerHTML;
                      }
                      map[key] = elementValue;
                    }
                  }
              });
              event.ports[0].postMessage({result : map});
            }
            else if (event.data.pictureModal) {
              Chatbot.showPictureModal(event.data.pictureModal);
            }
            else if (event.data.jsevent) {
              Chatbot.startJsEvent(event.data.jsevent);
            }
          },
          false
        );
      }

      Chatbot = {
        init(param) {
          _initParam = param;
          addImageViewerModal();
          _initIframeListener();
          _createIframe();
          _iframe.addEventListener('load', function() {
            _createFlottingButton();
            if (sessionStorage.showChatbot !== undefined) {
              if (sessionStorage.showChatbot === 'true') {
                Chatbot.show();
              }
            } else {
              checkIfConversationAlreadyExists();
            }
          });
        },

        show() {
          sessionStorage.showChatbot = true;
          _iframe.contentWindow.postMessage('start', '*');
          _iframe.style.visibility = 'visible';
        },

        showPictureModal(src) {
          const modal = document.getElementById('imageViewerModal');
          const modalImg = parent.document.getElementById('imgToView');
          modalImg.src = src;
          modal.style.display = 'block';
        },

        minimize() {
          sessionStorage.showChatbot = false;
          _iframe.style.visibility = 'hidden';
        },

        close() {
          sessionStorage.showChatbot = false;
          document.body.removeChild(_iframe);
          _iframe = null;
        },

        clearSessionStorage() {
          sessionStorage.clear();
          _iframe.contentWindow.postMessage('clearSessionStorage', '*');
        },

        startJsEvent(eventName) {
          if (eventName === 'welcomeTour1') {
            tour.start();
          }
        }
      };
    }

    _chatbotWrapper();
});

