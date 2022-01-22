VUiExtensions.methods.calendarNext = function() {
                this.$refs.calendar.next()
            };
            VUiExtensions.methods.calendarPrev  = function() {
                this.$refs.calendar.prev()
            };
            var PARSE_REGEX = /^(\d{1,2})\/(\d{1,2})\/(\d{4})?([^\d]+(\d{1,2}))?(:(\d{1,2}))?(:(\d{1,2}))?(.(\d{1,3}))?$/;
            VUiExtensions.methods.toCalendarDate  = function(vDate) {
                 // DD/MM/YYYY hh:mm
                var parts = PARSE_REGEX.exec(vDate);
                if (!parts) { return null }

                return {
                  date: parts[3]+'-'+parts[2]+'-'+parts[1],
                  time: parts[5] + ':' +  parts[7],
                  year: parseInt(parts[3], 10),
                  month: parseInt(parts[2], 10),
                  day: parseInt(parts[1], 10) || 1,
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
 
                
            VUiExtensions.methods.isCssColor  = function(color) {
                return !!color && !!color.match(/^(#|(rgb|hsl)a?\()/)
              };

              VUiExtensions.methods.badgeClasses  = function(event, type) {
                const cssColor = this.isCssColor(event.bgcolor)
                const isHeader = type === 'header'
                return {
                  [`text-white bg-${event.bgcolor} event-${event.eventStatusId} `]: !cssColor,
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
