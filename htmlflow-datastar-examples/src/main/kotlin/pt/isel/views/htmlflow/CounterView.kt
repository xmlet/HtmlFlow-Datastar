package pt.isel.views.htmlflow

import htmlflow.HtmlView
import htmlflow.dyn
import htmlflow.span
import htmlflow.view

val hfCounterEventView: HtmlView<Int> =
    view {
        span {
            attrId("counter")
            dyn { count: Int ->
                text(count.toString())
            }
        }
    }
