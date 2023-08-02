package com.festago.festago.presentation.ui.home.ticketlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.festago.festago.domain.repository.TicketRepository
import com.festago.festago.presentation.mapper.toPresentation
import com.festago.festago.presentation.util.MutableSingleLiveData
import com.festago.festago.presentation.util.SingleLiveData
import kotlinx.coroutines.launch

class TicketListViewModel(
    private val ticketRepository: TicketRepository,
) : ViewModel() {

    private val _uiState = MutableLiveData<TicketListUiState>()
    val uiState: LiveData<TicketListUiState> = _uiState

    private val _event = MutableSingleLiveData<TicketListEvent>()
    val event: SingleLiveData<TicketListEvent> = _event

    fun loadTickets() {
        viewModelScope.launch {
            _uiState.value = TicketListUiState.Loading
            ticketRepository.loadTickets()
                .onSuccess { tickets ->
                    _uiState.value = TicketListUiState.Success(tickets.toPresentation())
                }.onFailure {
                    _uiState.value = TicketListUiState.Error
                }
        }
    }

    fun showTicketEntry(ticketId: Long) {
        _event.setValue(TicketListEvent.ShowTicketEntry(ticketId))
    }

    class TicketListViewModelFactory(
        private val ticketRepository: TicketRepository,
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TicketListViewModel::class.java)) {
                return TicketListViewModel(ticketRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel Class")
        }
    }
}