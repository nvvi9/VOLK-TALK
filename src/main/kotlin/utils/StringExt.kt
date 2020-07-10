package utils

val String.quote
    get() = this.substringAfter("<div class=\"text\">")
        .substringBefore("</div><a class=")

val String.imageUri
    get() = "http://neurovolk.xyz" + this.substringAfter("<img class=\"generated\" src=\"")
        .substringBefore("\"/><br/><a class=\"index\" href=\"")

val String.speechUri
    get() = "http://translate.google.com/translate_tts?ie=UTF-8&tl=ru-RU&client=tw-ob&q=$this"

val String.asCode
    get() = "```$this```"
