require('zappajs') process.env.IP, 7373, ->
    @use 'partials'
    @use 'bodyParser', static: __dirname + '/public'
    @use require('connect-assets')
        src: './webserver/assets'

    @io.set 'log level', 1

    statusUpdateClientSocket = undefined
    freezeTimeSessionId = undefined
    sessionDirPath = undefined
    fsLib = require("fs")
    systemMessagesJSON = JSON.decode(fsLib.readFileSync("./socketMessages.json", "utf8"))
    masterMessages = systemMessagesJSON.masterMessages
    picTakerMessages = systemMessagesJSON.picTakerMessages

    sendClientMsg = (msgString, payloadData) ->
        statusUpdateClientSocket.emit 'update', {msg: msgString, payload: payloadData}

    @on 'idClientConnection': ->
        statusUpdateClientSocket = @socket

    @on 'systemMsg': ->
        switch @data.msg
            when masterMessages.register then sendClientMsg "registerMasterTEMP"
            when masterMessages.initPicTakerOrder then do ->
                freezeTimeSessionId = "FT3D-" + new Date().getTime()
                sessionDirPath = "./sessions/" + freezeTimeSessionId
                fsLib.mkdirSync sessionDirPath
                sendClientMsg "initPicTakerOrderTEMP"
            #masterMessages.startFrameCapture
            #masterMessages.resetSystem
            when picTakerMessages.register then sendClientMsg "picTakerHasRegisteredTEMP"
            when picTakerMessages.requestFrameOrder then sendClientMsg "picTakerHasOrderedTEMP", @data.payload
            when picTakerMessages.picTakingReady then sendClientMsg "picTakerIsReadyTEMP", @data.payload

    @view index: ->

    @get '/': ->
        @render 'index'

    @post '/fileUpload': ->
        uploadedFrameInfo = JSON.parse @request.query.info

        fsLib.readFile @request.files.framePic.path, (err, data) ->
            saveImagePath = sessionDirPath + "/frame" + uploadedFrameInfo.frameNumber + ".jpg"
            fsLib.writeFile saveImagePath, data, (err) ->
                console.log "Frame " + uploadedFrameInfo.frameNumber + " successfully uploaded."

    @view layout: ->
        doctype 5
        html ->
            head ->
                title 'FreezeTime3D'
                link href: 'http://yui.yahooapis.com/3.8.0/build/cssreset/cssreset-min.css', rel: 'stylesheet'
                script src: 'http://ajax.googleapis.com/ajax/libs/jquery/1.8.3/jquery.min.js'
                link href: '/css/styles.css', rel: 'stylesheet'
                script src: '/socket.io/socket.io.js'
                js 'app'
            body ->
                h1 'FreezeTime3D'
                p class: "status-field"
                p class: "pic-takers-label", -> "Pic Takers:"
                div class: "grid-container", ->


