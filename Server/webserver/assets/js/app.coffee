###
Client side code to be run in a compiled js file via connect-assets
###

#socket = io.connect 'http://localhost:7373'

#socket.on('news', function (data) {
    #console.log(data);
    #socket.emit('my other event', { my: 'data' });
#});

$ ->
    statusPrefix = "Status: "

    statusField = $(".status-field")
    statusField.html statusPrefix + "Waiting to get started"

    #siteSocket = io.connect 'http://localhost:7373'

    siteSocket.on 'connect', ->
        siteSocket.emit 'id', {client: 'website'}

    siteSocket.on 'update', (data) ->
        #switch data
        #    when "something" then someFunc
        #    when "else" then someOtherFunc