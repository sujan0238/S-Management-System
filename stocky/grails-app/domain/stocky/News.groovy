package stocky

class News {

    String title
    String content
    String photo

    static mapping = {
        content type: 'text'
    }

    static constraints = {
    }
}
