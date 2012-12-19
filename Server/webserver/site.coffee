require('zappajs') process.env.IP, 7373, ->
    @use 'partials'
    @use 'bodyParser', static: __dirname + '/public'
    @use require('connect-assets')
        src: './webserver/assets'

    statusUpdateClientSocket = undefined

    @on 'idClientConnection': ->
        statusUpdateClientSocket = @socket

    @on 'systemMsg': ->
        switch @data.msg
            when "masterRegister" then statusUpdateClientSocket.emit 'update', {msg: "aTestUpdate"}
            #when "else" then somethingElse

    @view index: ->

    @get '/': ->
        @render 'index'

#    @get '/upload': ->
#        @render 'upload'

#    @view upload: ->
#        form method: 'post', action: '/fileUpload', enctype: 'multipart/form-data', ->
#            input
#                id: 'myFile'
#                type: 'file'
#                name: 'myFile'
#                placeholder: 'File Path'
#                size: 50
#                value: @myFile
#            button 'Upload File'

    #@post '/fileUpload': ->
        #    @send @request.files
        #fs.readFile(req.files.displayImage.path, function (err, data) {
        #    var newPath = __dirname + "/uploads/uploadedFileName";
        #        fs.writeFile(newPath, data, function (err) {
        #        res.redirect("back");
        #    });
        #});

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
                div class: "grid-container", ->
                    #div class: "pic-taker-cell", -> p "1"

