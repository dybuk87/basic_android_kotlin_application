# Basic Android Kotlin Application

This is simple kotlin application that show list of post in first activity, and post comments in next activity that is open after user click in post.

Data source depend on build flavor:
- Mock state - generate initial random data, and  slowly append data during runtime to test list update
- Dev state - connect to online resource and fetch data from url :   
GET http://jsonplaceholder.typicode.com/posts  
GET http://jsonplaceholder.typicode.com/users  
GET http://jsonplaceholder.typicode.com/comments  


# Project structure

Project is divided into two parts
- core - This is android module that wrap all classes that are application logic (exclude activities/views etc.) including bussines logic, data access layer, anti corruption layer
- app - this is actual android part of application view only part.

# Core module

## Core Commons

Commons are classes that can be use in all application to reduce code redundancy. 
- AsyncList<T : AsyncListElement> - This class handle asynchronus list that distribute it state by BehaviorSubject, and also allow to wait on expected item using Rx.Observable
- CombineFlatMap - This class allow to merge multiple observables by using stream flatMap, this merge is dynamic allowing to exclude some observable during runtime. CombineFlatMap use sort of builder design pattern to combine.
- SimpleFuture - Implements java concurrency Future, allow to wait for result/error using get, and allow to set result/error by methods setResult/setError. When error is set get will throw ExecuteException and with cause by set in setError()
- Translator<A, B> - interface to translate forward from object A to object B and backward from B to A
  
## Service 

Currently one service to handle "chat". This service allow to fetch users/posts/comments 
Service interface separate data source from application this is DAL (Data Access Layer)

This is a place where you can add low level cache / merge multiple data source etc.

Service use POJO/Transfer object.

```
interface ChatService {
    fun getUsers() : Observable<List<UserDto>>

    fun getPosts() : Observable<List<PostDto>>

    fun getComments() : Observable<List<CommentDto>>

}
```

Service have two implementation
- First delegate all operations to Retrofit

```
class ChatServiceImpl(private val chatServiceRetrofit: ChatServiceRetrofit) : ChatService {

    override fun getUsers(): Observable<List<UserDto>> = chatServiceRetrofit.getUsers()

    override fun getPosts(): Observable<List<PostDto>> = chatServiceRetrofit.getPosts()

    override fun getComments(): Observable<List<CommentDto>> = chatServiceRetrofit.getComments()

}
```


- Second is mock implementation
```
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
     ...
```

## Chat Repository - Model

Chat repository is application model for chat, repository can be shared between activities. Each activity create own ViewModel that use ChatRepository. 

Chat repository should be able to recover when wiped by android.

Repository should wrap one domain (DDD like)

Repository is also place for ACL (Anti Corruption Layer).

Repository can emmit domain events, for example if we create StatisticRepository that fetch statistic from server and we would like to know if new post was created we could emmit NewPost event from ChatRepository that StatisticRepository will consume and refresh statistics.  

Repository main tasks:
 - Fetch data using injected DAL,
 - Translate POJO into busines objects (for example PostDto -> Post) using Translators (ACL),
 - This is place to add high level cache / flyweigh patter 

```
interface ChatRepository  {

    fun getAllPosts() : AsyncList<Post>

    fun reload()

    fun findPostById(id : Id) : Observable<Post>

}
```

- reload - force data reload 
- getAllPosts() - return State-Asynchronous list of Posts, can be updated at any time, method return immediately, and can start data fetching if data are not ready. 
- findPostById(id) - return immediately "promise" of post object, if post list is not ready it will nitialize data fetch.

Post comments are exposed by post.comments this is AsyncList<Comment>()

Like in DDD object are divided into :
- entities - object that are identify by Id
- values - object that are identified by value (immutable)


# Application module

Application use ChatRepository to handle data for PostListActivity and CommentListActivity.
ChatRepository is Model for M-V-VM, for this simple activity View is activity, ViewModel is android architecture ViewModel.

```
class PostListViewModel : ViewModel() {

    val postList: MutableLiveData<AsyncListState<Post>> = MutableLiveData()

    val searchFilter: MutableLiveData<String> = MutableLiveData()

    var chatRepository: ChatRepository? = null

    var postListConsumer: Disposable? = null

    fun subscribe(chatRepository: ChatRepository) {
        this.chatRepository = chatRepository
        postListConsumer = this.chatRepository?.getAllPosts()?.state?.subscribe(
            { onPostListUpdate(it) },
            { it.printStackTrace() })
    }

    fun unsubscribe() {
        postListConsumer?.dispose()
        postListConsumer = null
    }

    @VisibleForTesting
    fun onPostListUpdate(postList: AsyncListState<Post>) {
        this.postList.postValue(postList)
    }

    fun updateTestFilter(text: String) {
        searchFilter.value = text
    }
}

```
ViewModel subscribe to BehaviorSubject from AsyncList, this will push every update as snapshot to ViewModel::onPostListUpdate this method will forward it to android MutableLiveData::postList
Activity bind to MutableLiveData::postList and will forward this list to adapter.

Adapter always work on list snapshot:

This is adapter update method:

```
  fun update(data: AsyncListState<Post>) {
        when (data.type) {
            AsyncListState.StateType.NOT_READY -> setNotReadyState()
            AsyncListState.StateType.LOADING -> addLoadingItems()
            AsyncListState.StateType.INITIALIZED -> setData(data.snapshot)
            AsyncListState.StateType.ERROR -> setErrorState()
        }
    }
```
First we check list state: NOT_READY, LOADING, INITIALIZED, ERROR and forward call to state implementation.

Post use it own list of PostListItem, this allow us to create item for error, basic, loader, empty. 
When we try to set error state we just have to create one item PostListErrorItem and push it to the list, thats it!

setData not only translate Post to PostListItem but also apply filter:

```
   fun setData(snapshot: List<Post>) {
        removeLoadingItems()

        all =
            Stream.of(snapshot).map { PostListItem(PostListItemType.BASIC, it) }.collect(Collectors.toList())

        updateFilter(this.filter)
        
    }
```

First we remove all loading items, then we translate all list items into "all" list, and then we invoke "updateFilter" method with current filter.

```
fun updateFilter(filter: Predicate<PostListItem>) {
        this.filter = filter

        val newFiltered = Stream.of(all).filter(filter).collect(Collectors.toList())

        val diff = DiffUtil.calculateDiff(PostListItemDiff(newFiltered, filtered), true)

        filtered.clear()
        filtered.addAll(newFiltered)

        if (!filtered.isEmpty()) {
            diff.dispatchUpdatesTo(this)
        } else {
            filtered.add(PostListItem(PostListItemType.EMPTY))
            notifyDataSetChanged()
        }

    }
```

Update filter set new filter, then apply filter to "all" list and get newFiltered list.
Now we use DiffUtils to calculate diff between current view and new one, and dispatch updates.

