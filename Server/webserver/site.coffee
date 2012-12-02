require('zappajs') process.env.IP, 7373, ->
    @use 'partials'
    @use 'bodyParser'

    @use require('connect-assets')
        src: './webserver/assets'

    @on connection: ->
        console.log "A connection has been made to us from: " + @id

    @on msg: ->
        console.log "Here is some data: " + @data.my

    @get '/': ->
        @render 'index'

    @view index: ->
        p "Welcome to FreezeTime3D!"

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
                script src: 'http://ajax.googleapis.com/ajax/libs/jquery/1.8.3/jquery.min.js'
                script src: '/socket.io/socket.io.js'
                js 'app'
            body ->
                h1 'FreezeTime3D'
                @body

