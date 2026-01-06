package org.mjdev.safedialer.helpers

class Merger<T>(
        val firstList: List<T>,
        val secondList: List<T>,
        val comparer: (f: T, s: T) -> MergerAction,
        val inserts: (List<T>) -> Unit,
        val updates: (List<T>) -> Unit,
        val removals: (List<T>) -> Unit,
    ) {
        private val _inserts = mutableListOf<T>()
        private val _updates = mutableListOf<T>()
        private val _removals = mutableListOf<T>()

        fun merge() {
            _inserts.clear()
            _updates.clear()
            _removals.clear()
            val processedFromFirst = mutableSetOf<T>()
            secondList.forEach { secondItem ->
                val matchingFirst = firstList.firstOrNull { firstItem ->
                    when (comparer(firstItem, secondItem)) {
                        MergerAction.NOTHING, MergerAction.UPDATE -> {
                            processedFromFirst.add(firstItem)
                            true
                        }

                        MergerAction.INSERT, MergerAction.REMOVE ->{  
                            false 
                        }
                    }
                }
                when {
                    matchingFirst == null -> _inserts.add(secondItem)
                    comparer(matchingFirst, secondItem) == MergerAction.UPDATE -> {
                        _updates.add(secondItem)
                    }
                }
            }
            firstList.filterNot { item ->
                item in processedFromFirst
            }.forEach {
                _removals.add(it)
            }
            inserts(_inserts)
            updates(_updates)
            removals(_removals)
        }
        
        enum class MergerAction {
            NOTHING,
            INSERT,
            UPDATE,
            REMOVE
        }
    }