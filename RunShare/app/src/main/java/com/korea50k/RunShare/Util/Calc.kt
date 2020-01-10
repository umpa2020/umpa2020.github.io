package com.korea50k.RunShare.Util

import java.util.*

class Calc(){
    companion object{
        fun minDouble(list:Array<Vector<Double>>) : Double{
            var min=list[0][0]
            for(i in list.indices){
                if(list[i].min()!! <min){
                    min= list[i].min()!!
                }
            }
            return min
        }
        fun maxDouble(list:Array<Vector<Double>>) : Double{
            var max=list[0][0]
            for(i in list.indices){
                if(list[i].max()!! >max){
                    max= list[i].max()!!
                }
            }
            return max
        }
    }
}