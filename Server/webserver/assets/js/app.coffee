###
Client side code to be run in a compiled js file via connect-assets
###

$ ->
    statusPrefix = "Status: "
    statusField = $(".status-field")
    gridContainer = $(".grid-container")

    init = ->
        @siteSocket = io.connect 'http://localhost:7373'
        do setupUpdateSocket
        updateStatusField "System ready, waiting to get started."

    updateStatusField = (msg) ->
        statusField.html statusPrefix + msg

    ###
    Setup the websocket connection with the running website
    ###
    setupUpdateSocket = ->
        siteSocket.on 'connect', ->
            siteSocket.emit 'idClientConnection'

        siteSocket.on 'update', (data) ->
            switch data.msg
                when "registerMasterFC" then updateStatusField "Master registered. Next up: Pic Taker ordering."
                when "initPicTakerOrderFC" then updateStatusField "Pic Taker ordering started. Master will tell you when to submit."
                when "picTakerHasRegisteredFC" then do addPicTakerStatusBox
                when "picTakerHasOrderedFC" then $(gridContainer.children("div").get(data.payload)).find("p").html("Ordered: " + data.payload)
                when "picTakerIsReadyFC" then $(gridContainer.children("div").get(data.payload)).find("p").html(data.payload + ": Ready")
                when "picProcessingFC" then $(gridContainer.children("div").get(data.payload)).find("p").html(data.payload + ": Processing")
                when "picProcessingCompleteFC" then $(gridContainer.children("div").get(data.payload)).html("<img src='/thumbs_temp/frame-thumb" + data.payload + ".jpg'>")

    ###
    Add a status box for a connected Pic Taker to the UI
    ###
    addPicTakerStatusBox = ->
        picTakerDiv = document.createElement 'div'
        picTakerDiv.className = "pic-taker-cell"
        labelP = document.createElement 'p'
        labelP.innerHTML = 'Registered'

        picTakerDiv.appendChild(labelP)
        gridContainer.append(picTakerDiv)

    do init