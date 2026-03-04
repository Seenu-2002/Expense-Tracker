package com.ajay.seenu.expensetracker.android.util

import com.ajay.seenu.expensetracker.domain.model.ExportData
import com.ajay.seenu.expensetracker.domain.model.TransactionExport
import java.io.ByteArrayOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * Generates a valid XLSX file as a byte array without external dependencies.
 * XLSX is a ZIP archive containing Office Open XML spreadsheet files.
 */
object ExcelGenerator {

    fun generate(exportData: ExportData): ByteArray {
        val baos = ByteArrayOutputStream()
        ZipOutputStream(baos).use { zip ->
            zip.putEntry("[Content_Types].xml", contentTypesXml())
            zip.putEntry("_rels/.rels", relsXml())
            zip.putEntry("xl/workbook.xml", workbookXml())
            zip.putEntry("xl/_rels/workbook.xml.rels", workbookRelsXml())
            zip.putEntry("xl/styles.xml", stylesXml())
            zip.putEntry("xl/worksheets/sheet1.xml", sheetXml(exportData))
        }
        return baos.toByteArray()
    }

    private fun ZipOutputStream.putEntry(name: String, content: String) {
        putNextEntry(ZipEntry(name))
        write(content.toByteArray(Charsets.UTF_8))
        closeEntry()
    }

    private fun contentTypesXml() = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
  <Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
  <Default Extension="xml" ContentType="application/xml"/>
  <Override PartName="/xl/workbook.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml"/>
  <Override PartName="/xl/worksheets/sheet1.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml"/>
  <Override PartName="/xl/styles.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.styles+xml"/>
</Types>"""

    private fun relsXml() = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="xl/workbook.xml"/>
</Relationships>"""

    private fun workbookXml() = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<workbook xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main" xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships">
  <sheets>
    <sheet name="Transactions" sheetId="1" r:id="rId1"/>
  </sheets>
</workbook>"""

    private fun workbookRelsXml() = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet" Target="worksheets/sheet1.xml"/>
  <Relationship Id="rId2" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles" Target="styles.xml"/>
</Relationships>"""

    private fun stylesXml() = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<styleSheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main">
  <fonts count="2">
    <font><sz val="11"/><name val="Calibri"/></font>
    <font><b/><sz val="11"/><name val="Calibri"/></font>
  </fonts>
  <fills count="2">
    <fill><patternFill patternType="none"/></fill>
    <fill><patternFill patternType="gray125"/></fill>
  </fills>
  <borders count="1">
    <border><left/><right/><top/><bottom/><diagonal/></border>
  </borders>
  <cellStyleXfs count="1">
    <xf numFmtId="0" fontId="0" fillId="0" borderId="0"/>
  </cellStyleXfs>
  <cellXfs count="2">
    <xf numFmtId="0" fontId="0" fillId="0" borderId="0" xfId="0"/>
    <xf numFmtId="0" fontId="1" fillId="0" borderId="0" xfId="0" applyFont="1"/>
  </cellXfs>
</styleSheet>"""

    private fun sheetXml(exportData: ExportData): String {
        val sb = StringBuilder()
        sb.append("""<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<worksheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main">
<sheetData>""")

        // Summary rows
        sb.appendRow(1, listOf(cell("Export Date", bold = true), cell(exportData.exportDate)))
        sb.appendRow(2, listOf(cell("Total Transactions", bold = true), cellNum(exportData.totalTransactions.toDouble())))
        sb.appendRow(3, listOf(cell("Total Income", bold = true), cellNum(exportData.totalIncome)))
        sb.appendRow(4, listOf(cell("Total Expense", bold = true), cellNum(exportData.totalExpense)))

        // Empty row
        sb.appendRow(5, emptyList())

        // Header row
        val headers = listOf("ID", "Amount", "Description", "Category", "Date", "Type")
        sb.appendRow(6, headers.map { cell(it, bold = true) })

        // Data rows
        exportData.transactions.forEachIndexed { index, tx ->
            sb.appendRow(
                7 + index,
                listOf(
                    cellNum(tx.id.toDouble()),
                    cellNum(tx.amount),
                    cell(tx.description ?: ""),
                    cellNum(tx.category.toDouble()),
                    cell(tx.date),
                    cell(tx.type.name)
                )
            )
        }

        sb.append("</sheetData></worksheet>")
        return sb.toString()
    }

    private fun StringBuilder.appendRow(rowNum: Int, cells: List<String>) {
        append("<row r=\"$rowNum\">")
        cells.forEachIndexed { colIndex, cellXml ->
            val colLetter = ('A' + colIndex)
            append(cellXml.replace("CELLREF", "$colLetter$rowNum"))
        }
        append("</row>")
    }

    private fun cell(value: String, bold: Boolean = false): String {
        val style = if (bold) " s=\"1\"" else ""
        val escaped = value
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
        return "<c r=\"CELLREF\" t=\"inlineStr\"$style><is><t>$escaped</t></is></c>"
    }

    private fun cellNum(value: Double): String {
        return "<c r=\"CELLREF\"><v>$value</v></c>"
    }
}
