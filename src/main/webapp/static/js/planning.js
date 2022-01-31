VUiExtensions.methods.calendarNext = function() {
                this.$refs.calendar.next()
            };
            VUiExtensions.methods.calendarPrev  = function() {
                this.$refs.calendar.prev()
            };
            
            VUiExtensions.methods.fromCalendarTime = function (date) {
                //return date.day+'/'+date.month+'/'+date.year+' '+date.hour+':'+date.minute;
                //2022-01-23T22:22:39.124Z
                ;
                return date.date+'T'+date.time+':00.000Z';
            }
            
             //var PARSE_REGEX = /^(\d{1,2})\/(\d{1,2})\/(\d{4})?([^\d]+(\d{1,2}))?(:(\d{1,2}))?(:(\d{1,2}))?(.(\d{1,3}))?$/;
             var PARSE_REGEX = /^(\d{1,4})-(\d{1,2})-(\d{2})?([^\d]+(\d{1,2}))?(:(\d{1,2}))?(:(\d{1,2}))?(.(\d{1,3}))Z?$/;
             VUiExtensions.methods.toCalendarDate  = function(vDate) {
                 // DD/MM/YYYY hh:mm
                var parts = PARSE_REGEX.exec(vDate);
                if (!parts) { return null }

                return {
                  date: parts[1]+'-'+parts[2]+'-'+parts[3],
                  time: parts[5] + ':' +  parts[7],
                  year: parseInt(parts[1], 10),
                  month: parseInt(parts[2], 10),
                  day: parseInt(parts[3], 10) || 1,
                  hour: parseInt(parts[5], 10) || 0,
                  minute: parseInt(parts[7], 10) || 0,
                  weekday: 0,
                  doy: 0,
                  workweek: 0,
                  hasDay: true,
                  hasTime: !!(parts[5] && parts[7]),
                  past: false,
                  current: false,
                  future: false,
                  disabled: false
                }
            };
 
            VUiExtensions.methods.loadEvents = function(url) {
                console.log('loadEvents:',url);
                this.$http.get(url)
                .then(function (response) {
                    this.$data.vueData.events = (response.data);
                }.bind(this)).catch(function (error) {
                    this.$q.notify(error.response.status + ":" + error.response.statusText + " Can't load events ");
                });
                           
            }
            
            VUiExtensions.methods.onClickCalendar = function(url, data) {
                console.log('click:time2:',url, data);
                this.$http.post(url, {'dateTime':this.fromCalendarTime(data.scope.timestamp), 'duration':59})
                .then(function (response) {
                    this.$data.vueData.events.push(response.data);
                }.bind(this)).catch(function (error) {
                    this.$q.notify(error.response.status + ":" + error.response.statusText + " Can't add event ");
                }.bind(this));
            }
            
            VUiExtensions.methods.onClickRemove = function(url, event) {
                console.log('onClickRemove:',url, event);
                this.$http.delete(url+event.baseId+'/'+event.eventId)
                .then(function (response) {
                    var i = this.$data.vueData.events.length;
                    while (i--) {
                        if (this.$data.vueData.events[i].eventId === response.data) {
                            this.$data.vueData.events.splice(i, 1);
                            break;
                        }
                    }
                }.bind(this)).catch(function (error) {
                    this.$q.notify(error.response.status + ":" + error.response.statusText + " Can't remove event ");
                }.bind(this));
            }
            
            VUiExtensions.methods.onClickUnReserve = function(url, event) {
                console.log('onClickUnReserve:',url, event);
                this.$http.post(url, event)
                .then(function (response) {
                    var i = this.$data.vueData.events.length;
                    while (i--) {
                        if (this.$data.vueData.events[i].eventId === response.data.eventId) {
                            this.$set(this.$data.vueData.events, i , response.data);
                            break;
                        }
                    }
                }.bind(this)).catch(function (error) {
                    this.$q.notify(error.response.status + ":" + error.response.statusText + " Can't remove event ");
                }.bind(this));
            }
            
            VUiExtensions.methods.onClickSelect = function(url, event) {
                console.log('onClickSelect:',url, event);
                this.$http.post(url, event)
                .then(function (response) {
                    var i = this.$data.vueData.events.length;
                    while (i--) {
                        if (this.$data.vueData.events[i].eventId === response.data.eventId) {
                            this.$set(this.$data.vueData.events, i , response.data);
                            //this.$data.vueData.events[i]=response.data;
                            break;
                        }
                    }
                }.bind(this)).catch(function (error) {
                    this.$q.notify(error.response.status + ":" + error.response.statusText + " Can't remove event ");
                }.bind(this));
            }
            VUiExtensions.methods.onClickPreSelect = function(event) {
                console.log('onClickPreSelect:',event);
                for(var k in event) this.$data.vueData.selectedEvent[k]=event[k];
                this.$data.dataX.selectEvent = true;
            }            
    
            VUiExtensions.methods.isCssColor  = function(color) {
                return !!color && !!color.match(/^(#|(rgb|hsl)a?\()/)
              };

              VUiExtensions.methods.badgeClasses  = function(event, type) {
                const cssColor = this.isCssColor(event.bgcolor)
                const isHeader = type === 'header'
                return {
                  [`event-${event.eventStatusId} `]: !cssColor,
                  'full-width': !isHeader && (!event.side || event.side === 'full'),
                  'left-side': !isHeader && event.side === 'left',
                  'right-side': !isHeader && event.side === 'right'                  
                }
              };

              VUiExtensions.methods.badgeStyles  = function(event, type, timeStartPos, timeDurationHeight) {
                const s = {}
                if (this.isCssColor(event.bgcolor)) {
                  s['background-color'] = event.bgcolor
                  s.color = luminosity(event.bgcolor) > 0.5 ? 'black' : 'white'
                }
                if (timeStartPos) {
                  s.top = timeStartPos(event.eventCalendar.time) + 'px'
                }
                if (timeDurationHeight) {
                  s.height = (timeDurationHeight(event.durationMinutes)-2) + 'px'
                }
                s['align-items'] = 'flex-start'
                return s
              };
              
            VUiExtensions.methods.getMyReservedEvents = function(allEvents, personId) {
                const selectedEvents = []
                for (let i = 0; i < allEvents.length; ++i) {
                  if (allEvents[i].personId===personId && allEvents[i].eventStatusId=='RESERVED') {
                    var eventCalendar = this.toCalendarDate(allEvents[i].dateTime);
                    allEvents[i].eventCalendar = eventCalendar;
                    selectedEvents.push(allEvents[i]);
                    }
                  }
                return selectedEvents
            };
              
            VUiExtensions.methods.getEvents  = function(dt, allEvents) {
                const currentDate = QCalendar.parsed(dt)
                const selectedEvents = []
                for (let i = 0; i < allEvents.length; ++i) {
                  let added = false
                  var eventCalendar = this.toCalendarDate(allEvents[i].dateTime);
                  allEvents[i].eventCalendar = eventCalendar;
                  if (eventCalendar.date === dt) {
                    if (eventCalendar.time) {
                      if (selectedEvents.length > 0) {
                        // check for overlapping times
                        const startTime = QCalendar.parsed(eventCalendar.date + ' ' + allEvents[i].eventCalendar.time)
                        const endTime = QCalendar.addToDate(startTime, { minute: allEvents[i].durationMinutes })
                        for (let j = 0; j < selectedEvents.length; ++j) {
                          if (selectedEvents[j].eventCalendar.time) {
                            const startTime2 = QCalendar.parsed(selectedEvents[j].eventCalendar.date + ' ' + selectedEvents[j].eventCalendar.time)
                            const endTime2 = QCalendar.addToDate(startTime2, { minute: selectedEvents[j].durationMinutes })
                            if (QCalendar.isBetweenDates(startTime, startTime2, endTime2, true) || QCalendar.isBetweenDates(endTime, startTime2, endTime2, true)) {
                                selectedEvents[j].side = 'left'
                                allEvents[i].side = 'right'
                                  selectedEvents.push(allEvents[i])
                              added = true
                              break
                            }
                          }
                        }
                      }
                    }
                    if (!added) {
                      allEvents[i].side = undefined
                      selectedEvents.push(allEvents[i])
                    }
                  }
                  else if (eventCalendar.days) {
                    // check for overlapping dates
                    const startDate = QCalendar.parsed(eventCalendar.date)
                    const endDate = QCalendar.addToDate(startDate, { day: eventCalendar.days })
                    if (QCalendar.isBetweenDates(currentDate, startDate, endDate)) {
                        selectedEvents.push(allEvents[i])
                      added = true
                    }
                  }
                }
                return selectedEvents
              };

        VUiExtensions.methods.getResourceImage  = function(resource) {
            return (resource.icon !== undefined ? resource.icon : resource.avatar !== undefined ? 'img:' + resource.avatar : '')
        }        
        
        VUiExtensions.dataX = {selectEvent : false}
    