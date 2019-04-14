package pl.dybuk.posttest.ui.view

import java.util.*

internal class ColorGenerator private constructor(private val mColors: List<Int>) {
    private val mRandom: Random = Random(System.currentTimeMillis())

    val randomColor: Int
        get() = mColors[mRandom.nextInt(mColors.size)]

    fun getColor(key: Any): Int {
        return mColors[Math.abs(key.hashCode()) % mColors.size]
    }

    companion object {

        var DEFAULT: ColorGenerator

        var MATERIAL: ColorGenerator

        init {
            DEFAULT = create(
                Arrays.asList(
                    -0xe9c9c,
                    -0xa7aa7,
                    -0x65bc2,
                    -0x1b39d2,
                    -0x98408c,
                    -0xa65d42,
                    -0xdf6c33,
                    -0x529d59,
                    -0x7fa87f
                )
            )
            MATERIAL = create(
                Arrays.asList(
                    -0x55352c,
                    -0x1c3096,
                    -0x6c3c85,
                    -0xf8383,
                    -0x164494,
                    -0x484a17
                )
            )
        }

        fun create(colorList: List<Int>): ColorGenerator {
            return ColorGenerator(colorList)
        }
    }
}