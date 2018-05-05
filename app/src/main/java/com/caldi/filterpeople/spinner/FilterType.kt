package com.caldi.filterpeople.spinner

sealed class FilterType(val text: String) {

    class NameFilterType(text: String) : FilterType(text)

    class LinkFilterType(text: String) : FilterType(text)

    class QuestionFilterType(text: String) : FilterType(text)
}