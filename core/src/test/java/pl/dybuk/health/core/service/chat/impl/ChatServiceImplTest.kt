package pl.dybuk.health.core.service.chat.impl

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.mockito.runners.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ChatServiceImplTest {

    @Test
    fun delegateTest() {
        val retrofit = Mockito.mock(ChatServiceRetrofit::class.java)

        val chatService = ChatServiceImpl(retrofit)

        chatService.getComments()
        Mockito.verify(retrofit, times(1)).getComments()

        chatService.getPosts()
        Mockito.verify(retrofit, times(1)).getPosts()

        chatService.getUsers()
        Mockito.verify(retrofit, times(1)).getUsers()
    }

}