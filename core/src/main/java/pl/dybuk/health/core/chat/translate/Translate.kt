package pl.dybuk.health.core.chat.translate

import com.annimon.stream.Stream
import pl.dybuk.health.core.chat.entity.Comment
import pl.dybuk.health.core.chat.entity.Post
import pl.dybuk.health.core.chat.entity.User
import pl.dybuk.health.core.chat.value.*
import pl.dybuk.health.core.common.translate.Translator
import pl.dybuk.health.core.service.chat.dto.CommentDto
import pl.dybuk.health.core.service.chat.dto.Geo
import pl.dybuk.health.core.service.chat.dto.PostDto
import pl.dybuk.health.core.service.chat.dto.UserDto

class UserTranslator : Translator<UserDto, User>() {

    override fun forward(source: UserDto): User =
        User(
            Id(source.id),
            source.name,
            source.username,
            Address(
                source.address.street,
                source.address.suite,
                source.address.city,
                source.address.zipcode,
                GeoLocation(
                    source.address.geo.lat,
                    source.address.geo.lng
                )
            ),
            Phone(source.phone),
            Website(source.website),
            Company(
                source.company.name,
                source.company.catchPhrase,
                source.company.bs
            )

        )

    override fun backward(source: User): UserDto {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

class PostTranslator(val userPool : List<User>) : Translator<PostDto, Post>() {

    override fun forward(source: PostDto): Post =
        Post(
            Id(source.id),
            Stream.of(userPool).filter { it.id.id  == source.userId }.findFirst().get(),
            source.title,
            source.body
        )

    override fun backward(source: Post): PostDto {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

class CommentTranslator : Translator<CommentDto, Comment>() {
    override fun forward(source: CommentDto): Comment =
        Comment(
            Id(source.id),
            source.name,
            Email(source.email),
            source.body
        )

    override fun backward(source: Comment): CommentDto {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}