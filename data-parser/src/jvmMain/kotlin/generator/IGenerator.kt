package generator

import androidx.compose.runtime.snapshots.SnapshotStateList

interface IGenerator<T> {
    fun generateOne(): T
    fun generate(len: Int): SnapshotStateList<T>
    fun serialize(arr: List<T>): String
}
