require('zappajs') process.env.IP, 7373, ->
    @enable 'zappa'
    @use 'partials'

    @get '/': ->
        @render 'index',

    @view layout: ->
        doctype 5
        html ->
            head ->
                title 'FreezeTime3D'
                script src: 'http://ajax.googleapis.com/ajax/libs/jquery/1.8.3/jquery.min.js'
                #script src: '/zappa/Zappa.js'
                script src: '/index.js'
            body ->
                h1 'FreezeTime3D'
                @body

    @view index: ->
        p "Shows what is going on with your Freeze-timing!"
