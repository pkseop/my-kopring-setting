package my.kopring.setting.excel

import my.kopring.setting.component.FileHandler
import my.kopring.setting.service.domain.ExcelUtils
import org.apache.commons.io.IOUtils
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.streaming.SXSSFWorkbook
import org.springframework.stereotype.Component
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@Component
class ExcelExample(
    private val fileHandler: FileHandler
) {
    fun excel() {
        fun download(serviceId: String, date: String, gmt: Double, lang: String): String {
            var page = 0L
            val limit = 100L
            var rownum = 0
            var column = 0

            var wb: SXSSFWorkbook? = null

            return try {
                wb = SXSSFWorkbook()
                val sheet = wb.createSheet()
                var row: Row
                val dateCellStyle: CellStyle = ExcelUtils.makeDateCellStyle(wb)

                row = sheet.createRow(rownum++)
                row.createCell(column++).setCellValue("col1")
                row.createCell(column++).setCellValue("col2")
                row.createCell(column++).setCellValue("col3")
                row.createCell(column++).setCellValue("col4")
                row.createCell(column++).setCellValue("col5")
                row.createCell(column++).setCellValue("col6")
                row.createCell(column++).setCellValue("col7")

                do {
                    for (num in 1..10) {
                        column = 0
                        row = sheet.createRow(rownum++)
                        ExcelUtils.setCellValue(row.createCell(column++), dateCellStyle, "val1")
                        ExcelUtils.setCellValue(row.createCell(column++), dateCellStyle, "val2")
                        ExcelUtils.setCellValue(row.createCell(column++), dateCellStyle, "val3")
                        ExcelUtils.setCellValue(row.createCell(column++), dateCellStyle, "val4")
                        ExcelUtils.setCellValue(row.createCell(column++), dateCellStyle, "val5")
                        ExcelUtils.setCellValue(row.createCell(column++), dateCellStyle, "val6")
                        ExcelUtils.setCellValue(row.createCell(column++), dateCellStyle, "val7")
                    }
                } while (false)

                val filename = "upload_${System.currentTimeMillis()}.xlsx"
                val file = File(fileHandler.localtempDir, filename)
                FileOutputStream(file).use { os -> wb.write(os) }

                val fileModel = fileHandler.toTemp(file, filename)

                fileModel.url
            } catch (e: IOException) {
                throw RuntimeException(e);
            } finally {
                wb?.dispose();
                IOUtils.closeQuietly(wb);
            }
        }
    }
}