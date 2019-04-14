package pl.dybuk.health.core

import io.reactivex.Observable
import pl.dybuk.health.core.chat.entity.Post
import pl.dybuk.health.core.service.chat.ChatService
import pl.dybuk.health.core.service.chat.dto.*

class ChatServiceMock : ChatService {
    override fun getPosts(): Observable<List<PostDto>> =
        Observable.create<List<PostDto>>  {
            val list : MutableList<PostDto> = mutableListOf(
                PostDto(1, 1, "TITLE 1", "BODY POST 1"),
                PostDto(1, 2, "TITLE 2", "BODY POST 2"),
                PostDto(1, 3, "TITLE 3", "BODY POST 3"),
                PostDto(2, 4, "TITLE 4", "BODY POST 4")
            )

            Thread.sleep(1500)

            it.onNext(list)
            it.onComplete()
        }

    override fun getComments(): Observable<List<CommentDto>> =
        Observable.create<List<CommentDto>>  {
            val list : MutableList<CommentDto> = mutableListOf(
                CommentDto(1, 1, "NAME 1", "EMAIL 1", "COMMENT 1 POST 1"),
                CommentDto(1, 2, "NAME 2", "EMAIL 2", "COMMENT 2 POST 1"),
                CommentDto(2, 3, "NAME 1", "EMAIL 1", "COMMENT 1 POST 2"),
                CommentDto(2, 4, "NAME 2", "EMAIL 2", "COMMENT 2 POST 2"),
                CommentDto(2, 5, "NAME 3", "EMAIL 3", "COMMENT 3 POST 2"),
                CommentDto(2, 6, "NAME 3", "EMAIL 3", "COMMENT 4 POST 2"),
                CommentDto(2, 7, "NAME 3", "EMAIL 3", "COMMENT 5 POST 2"),
                CommentDto(2, 8, "NAME 2", "EMAIL 2", "COMMENT 6 POST 2"),
                CommentDto(3, 9, "NAME 2", "EMAIL 2", "COMMENT 1 POST 3"),
                CommentDto(3, 10, "NAME 1", "EMAIL 1", "COMMENT 2 POST 3"),
                CommentDto(3, 11, "NAME 3", "EMAIL 3", "COMMENT 3 POST 3")
            )

            Thread.sleep(1500)

            it.onNext(list)
            it.onComplete()
        }

    override fun getUsers(): Observable<List<UserDto>> =
        Observable.create<List<UserDto>>  {
            val list : MutableList<UserDto> = mutableListOf(
                UserDto(1, "LOGIN 1", "USER 1",
                    AddressDto("STREET 1", "SUITE 1", "CITY 1", "11-111", Geo("111", "111")),
                    "111-111-111", "aaaa@aaa.aa",
                    CompanyDto("COMPANY 1", "PHRASE 1", "BS 1")
                ),

                UserDto(2, "LOGIN 2", "USER 2",
                    AddressDto("STREET 2", "SUITE 2", "CITY 2", "22-222", Geo("222", "222")),
                    "222-222-222", "bbbb@bb.b",
                    CompanyDto("COMPANY 2", "PHRASE 2", "BS 2")
                )

            )

            Thread.sleep(1000)

            it.onNext(list)
            it.onComplete()
        }
}