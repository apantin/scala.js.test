package example

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.PackageBase.VdomAttr
import japgolly.scalajs.react.vdom.{TagMod, TagOf}
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom.html
import org.scalajs.dom.html.{Input, TableCell, TableHeaderCell}

case class ExcelDemoState(headers: Seq[String],
                          data: Seq[Seq[String]],
                          allData: Seq[Seq[String]],
                          sortBy: Option[Int],
                          descending: Boolean,
                          edit: Option[EditState],
                          search: Boolean)

case class EditState(cell: Int, row: Int)

class ExcelDemo($: BackendScope[Unit, ExcelDemoState]) {

    def rowAttr = VdomAttr("data-row")
    def idxAttr = VdomAttr("data-idx")

    def render(state: ExcelDemoState): TagOf[html.Element] = {
        <.div(
            renderToolbar(state),
            renderTable(state)
        )
    }

    private def renderToolbar(state: ExcelDemoState): TagOf[html.Element] = {
        <.button(^.`class` := "btn btn-primary", ^.onClick --> toggleSearch, "Фильтр")
    }

    private def renderFilter(state: ExcelDemoState): TagOf[html.Element] = {
        <.tr(^.onChange ==> changeFilter)(
            state.headers.zipWithIndex.map { case (_, index) =>
                <.td(
                    <.input(
                        ^.`type` := "text",
                        idxAttr := index
                    )
                )
            }: _*
        )
    }

    private def changeFilter(e: ReactEventFromInput): Callback = {
        val searchIndex = e.target.getAttribute(idxAttr.name).toInt
        val searchValue = e.target.value.toLowerCase
        $.state.flatMap { state =>
            $.modState(_.copy(data = state.allData.filter(_(searchIndex).toLowerCase.contains(searchValue))))
        }
    }

    private def renderTable(state: ExcelDemoState): TagOf[html.Element] = {
        <.table(^.`class` := "table",
            <.thead(^.onClick ==> handleThreadOnClick)(
                <.tr(
                    state.headers.zipWithIndex.map { case (title, index) =>
                        val sortTitle = state.sortBy.map { sortBy =>
                            if (index == sortBy) {
                                if (state.descending) "\u2191" else "\u2193"
                            } else ""
                        }.getOrElse("")
                        <.th(title + sortTitle)
                    }: _*
                )
            ),
            <.tbody(^.onDoubleClick ==> handleDoubleClick)(
                (if (state.search) Seq(renderFilter(state)) else Nil) ++
                state.data.zipWithIndex.map { case (row, rowIndex) =>
                    <.tr(^.key := rowIndex)(
                        row.zipWithIndex.map { case (cell, cellIndex) =>

                            val content: TagMod = state.edit.filter(edit => edit.cell == cellIndex && edit.row == rowIndex) match {
                                case Some(_) =>
                                    <.form(^.onSubmit ==> handleEditSubmit,
                                        <.input(
                                            ^.`type` := "text",
                                            ^.defaultValue := cell
                                        )
                                    )
                                case None =>
                                    vdomNodeFromString(cell)
                            }

                            <.td(^.key := cellIndex, rowAttr := rowIndex, content)
                        }: _*
                    )
                }: _*
            )
        )
    }

    private def setCellValue(data: Seq[Seq[String]], col: Int, row: Int, value: String): Seq[Seq[String]] = {
        println(s"$col $row $value")

        val newRow = data(row).patch(col, Seq(value), 1)
        data.patch(row, Seq(newRow), 1)
    }

    private def handleEditSubmit(e: ReactEventFromInput): Callback = {
        val newValue = e.target.firstChild.asInstanceOf[Input].value
        e.preventDefaultCB >> $.state.flatMap { state =>
            state.edit.map { edit =>
                $.modState(state => state.copy(edit = None, data = setCellValue(state.data, edit.cell, edit.row, newValue)))
            }.getOrElse(Callback())
        }
    }

    private def toggleSearch(): Callback = {
        $.state.flatMap { state =>
            $.modState(_.copy(search = !state.search))
        }
    }

    private def handleDoubleClick(e: ReactMouseEventFrom[TableCell]): Callback = {
        val cellIndex = e.target.cellIndex
        val rowIndex = e.target.getAttribute(rowAttr.name).toInt

        $.modState(_.copy(edit = Some(EditState(
            cell = cellIndex,
            row = rowIndex)))
        )
    }

    private def handleThreadOnClick(e:  ReactMouseEventFrom[TableHeaderCell]): Callback = {
        val index = e.target.cellIndex

        def sort(data: Seq[Seq[String]], descending: Boolean) = {
            if (descending) {
                data.sortBy(_(index)).reverse
            } else {
                data.sortBy(_(index))
            }
        }

        $.state.flatMap { state =>
            val descending = state.sortBy.contains(index) && !state.descending
            $.modState(_.copy(data = sort(state.data, descending), sortBy = Some(index), descending = descending))
        }
    }
}

object ExcelDemo {

    object default {
        val headers: Seq[String] = Seq("Книга", "Автор", "Язык", "Дата публикации", "Стоимость")

        val data: Seq[Seq[String]] = Seq(
            Seq("React.js быстрый старт", "Стоян Стефанов", "Русский", "2017", "350"),
            Seq("Создание микросервисов", "Сэм Ньюмен", "Русский", "2016", "800"),
            Seq("Сжатие данных, изображений и звука", "Д. Сэломон", "Русский", "2006", "200"),
            Seq("Немецкая пехота. Стратегические ошибки вермахта", "Максимилиан Фреттер-Пико", "Русский", "2013", "150"),
            Seq("Основы экономики", "Джон Сломан", "Русский", "2005", "620")
        )

        var state: ExcelDemoState = ExcelDemoState(headers = ExcelDemo.default.headers,
            data = ExcelDemo.default.data,
            allData = ExcelDemo.default.data,
            sortBy = None,
            descending = false,
            edit = None,
            search = false)
    }
}
