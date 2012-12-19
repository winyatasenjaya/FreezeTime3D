###
Client side code to be run in a compiled js file via connect-assets
###

$ ->
    statusPrefix = "Status: "
    statusField = $(".status-field")

    siteSocket = io.connect 'http://localhost:7373'

    siteSocket.on 'connect', ->
        siteSocket.emit 'idClientConnection'

    siteSocket.on 'update', (data) ->
        switch data.msg
            when "aTestUpdate" then statusField.html statusPrefix + "Waiting to get started"
            #when "else" then somethingElse