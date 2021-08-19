package com.example.test_exchangerate.ui.rest


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.ResourceManagerInternal.get
import androidx.lifecycle.*
import com.example.test_exchangerate.data.model.Rates
import com.example.test_exchangerate.repository.MainRepository
import com.example.test_exchangerate.util.DataState
import dagger.assisted.Assisted
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class MainViewModel @Inject constructor(
private val mainRepository: MainRepository,
 private val savedStateHandle: SavedStateHandle
): ViewModel() {

    private val _dataState: MutableLiveData<DataState<Rates>> = MutableLiveData()

    val dataState: LiveData<DataState<Rates>>
    get() = _dataState

    @RequiresApi(Build.VERSION_CODES.O)
    fun setStateEvent(mainStateEvent: MainStateEvent){
        viewModelScope.launch {
            when(mainStateEvent){
                is MainStateEvent.GetRatesEvent -> {
                    mainRepository.getRates()
                        .onEach {dataState ->
                            _dataState.value = dataState
                        }
                        .launchIn(viewModelScope)
                }

                MainStateEvent.None -> {
                    // who cares
                }
            }
        }
    }

}


sealed class MainStateEvent{

    object GetRatesEvent: MainStateEvent()

    object None: MainStateEvent()
}