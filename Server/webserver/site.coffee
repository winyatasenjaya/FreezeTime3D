require('zappajs') "localhost", 7373, ->
    @use 'partials'

    @coffee '/index.js': ->
        console.log "Client script from server"

    @get '/': ->
        @render 'index',

    @view layout: ->
        doctype 5
        html -
            head ->
                title 'FreezeTime3D'
                script src: '/index.js'
            body ->
                h1 'FreezeTime3D'
                @body

    @view index: ->
        p "Status updates show here"
