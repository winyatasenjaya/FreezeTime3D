require('zappajs') process.env.IP, 7373, ->
    @use 'partials'
    @use 'bodyParser', static: __dirname + '/public'
    @use require('connect-assets')
        src: './webserver/assets'

    @io.set 'log level', 1

    fsLib = require("fs")
    imageLib = require("imagemagick")
    fsExtras = require('fs.extra')

    statusUpdateClientSocket = undefined
    freezeTimeSessionId = undefined
    sessionDirPath = undefined
    systemMessagesJSON = JSON.decode(fsLib.readFileSync("./socketMessages.json", "utf8"))
    masterMessages = systemMessagesJSON.masterMessages
    picTakerMessages = systemMessagesJSON.picTakerMessages

    sendClientMsg = (msgString, payloadData) ->
        if statusUpdateClientSocket
            statusUpdateClientSocket.emit 'update', {msg: msgString, payload: payloadData}

    @on 'idClientConnection': ->
        statusUpdateClientSocket = @socket

    @on 'systemMsg': ->
        switch @data.msg
            when masterMessages.register then sendClientMsg "registerMasterFC"
            when masterMessages.initPicTakerOrder then do ->
                freezeTimeSessionId = "FT3D-" + new Date().getTime()
                sessionDirPath = "./sessions/" + freezeTimeSessionId
                fsLib.mkdirSync sessionDirPath
                sendClientMsg "initPicTakerOrderFC"
            #masterMessages.startFrameCapture #TODO: We do want the website UI to respond when the master kicks of the event!
            #masterMessages.resetSystem
            when picTakerMessages.register then sendClientMsg "picTakerHasRegisteredFC"
            when picTakerMessages.requestFrameOrder then sendClientMsg "picTakerHasOrderedFC", @data.payload
            when picTakerMessages.picTakingReady then sendClientMsg "picTakerIsReadyFC", @data.payload

    @view index: ->

    @get '/': ->
        @render 'index'

    @post '/fileUpload': (req, res) ->
        uploadedFrameInfo = req.body.info

        console.log '--- Yes we got here!!!! ---'
        sendClientMsg "picProcessingFC", uploadedFrameInfo.frameNumber

        fsLib.readFile @request.files.framePic.path, (err, data) ->
            saveImagePath = sessionDirPath + "/frame" + uploadedFrameInfo.frameNumber + ".jpg"
            thumbImgName = "frame-thumb" + uploadedFrameInfo.frameNumber + ".jpg"
            thumbImagePath = sessionDirPath + "/" + thumbImgName

            fsLib.writeFile saveImagePath, data, (err) ->
                console.log "Frame " + uploadedFrameInfo.frameNumber + " successfully uploaded"
                imageResizeOpts =
                    srcPath: saveImagePath
                    dstPath: thumbImagePath
                    width: 177
                    height: 100
                #TODO: Next up, need to get imgmagick working
                #imageLib.resize imageResizeOpts, (err, stdout, stderr) ->
                #    fsExtras.copy thumbImagePath, "./webserver/public/thumbs_temp/" + thumbImgName, (err) ->
                #        sendClientMsg "picProcessingCompleteFC", uploadedFrameInfo.frameNumber

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


