@file:Suppress("UnsafeCastFromDynamic")

package App.Product

/**
 * A thinking-in-react implementation by Scott_Huang@qq.com (Zhiliang.Huang@gmail.com)
 * This is a port of https://reactjs.org/docs/thinking-in-react.html
 *
 * Date: Nov 24, 2017
 */

import kotlinext.js.*
import kotlinx.html.*
import kotlinx.html.js.*
import org.w3c.dom.*
import react.*
import react.dom.*

fun RBuilder.productCategoryRow(category: String) {
    tr {
        th {
            attrs.colSpan = "2"
            +category
        }
    }
}

fun RBuilder.productRow(name: String, price: Double, isStocked: Boolean) {
    tr {
        td {
            if (isStocked)
                +name
            else
                span {
                    attrs.jsStyle = js {
                        color = "red"
                    }
                    +name
                }
        }
        td {
            +"$$price"
        }
    }
}

fun RBuilder.productTable(products: Array<PRODUCT>, filterText: String, inStockOnly: Boolean) {
    var lastCategory = ""

    table {
        thead { tr { th { +"Name" }; th { +"Price" } } }
        tbody {
            products.forEach {
                //show the category name
                if (it.category != lastCategory) {
                    productCategoryRow(it.category)
                }
                lastCategory = it.category

                //show the product per filter zipCode and inStock flag
                if (!(
                        (filterText.isNotEmpty() and !it.name.toUpperCase().contains(filterText.toUpperCase())) or
                                (inStockOnly and !it.isStocked))
                        ) {
                    productRow(it.name, it.price, it.isStocked)
                }
            }
        }
    }
}

interface SearchBarProps : RProps {
    var onClick: () -> Unit
    var onChange: (String) -> () -> Unit
    var inStockOnly: Boolean
    var filterText: String
}

class SearchBar(props: SearchBarProps) : RComponent<SearchBarProps, RState>(props) {
    override fun RBuilder.render() {
        div {
            input(type = InputType.text, name = "filterText") {
                attrs {
                    value = props.filterText
                    placeholder = "Search products"
                    onChangeFunction = {
                        val target = it.target as HTMLInputElement
                        props.onChange(target.value)()
                    }
                }
            }
            p {
                input(type = InputType.checkBox, name = "showInstockOnly") {
                    attrs {
                        value = if (props.inStockOnly) "1" else "0"
                        onClickFunction = {
                            props.onClick()
                        }
                    }
                }
                +"Show in-stock products only"
            }
        }
    }
}

fun RBuilder.searchBar(initialInStockOnly: Boolean,
                       initialFilterText: String,
                       handleFilterInputChange: (String) -> () -> Unit,
                       handleInStockInputClick: () -> () -> Unit
) = child(SearchBar::class) {
    attrs.onClick = handleInStockInputClick()
    attrs.onChange = handleFilterInputChange
    attrs.inStockOnly = initialInStockOnly
    attrs.filterText = initialFilterText
}


interface ProductProps : RProps {
}

interface ProductState : RState {
    var filterText: String
    var inStockOnly: Boolean
}

data class PRODUCT(val category: String, val price: Double, val isStocked: Boolean, val name: String)

var products = arrayOf(
        PRODUCT("Sporting Goods", 49.9, true, "Football"),
        PRODUCT("Sporting Goods", 9.9, true, "Baseball"),
        PRODUCT("Sporting Goods", 29.9, false, "Basketball"),
        PRODUCT("Electronics", 99.9, true, "iPod Touch"),
        PRODUCT("Electronics", 999.9, false, "iPhone X"),
        PRODUCT("Electronics", 199.9, true, "Nexus")
)

class Product(props: ProductProps) : RComponent<ProductProps, ProductState>(props) {
    override fun ProductState.init(props: ProductProps) {
        filterText = ""
        inStockOnly = false
    }

    private fun handleFilterInputChange(targetValue: String) = {
        setState {
            filterText = targetValue
        }
    }

    private fun handleInStockInputClick() = {
        setState {
            inStockOnly = !inStockOnly
        }
    }

    override fun RBuilder.render() {
        div {
            searchBar(state.inStockOnly, state.filterText,
                    { filterText: String -> handleFilterInputChange(filterText) },
                    { handleInStockInputClick() })
            productTable(products, state.filterText, state.inStockOnly)
        }
    }
}

fun RBuilder.product() = child(Product::class) {

}