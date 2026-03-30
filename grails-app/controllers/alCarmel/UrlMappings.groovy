package alCarmel

class UrlMappings {

    static mappings = {
        "/"(controller:"dashboard", action:"index")

        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }
        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}
