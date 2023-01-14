package my.kopring.setting.service.domain

import my.kopring.setting.exception.BadRequestException
import org.apache.commons.lang3.StringUtils
import org.apache.poi.ss.usermodel.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Date

class ExcelUtils(

) {
    companion object {
        var DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
        fun makeDateCellStyle(wb: Workbook): CellStyle {
            val cs: CellStyle = wb.createCellStyle()
            val createHelper: CreationHelper = wb.creationHelper

            cs.dataFormat = createHelper.createDataFormat().getFormat("yyyy-MM-dd HH:mm:ss")
            cs.wrapText = true
            cs.verticalAlignment = VerticalAlignment.CENTER

            return cs
        }

        fun setCellValue(cell: Cell, dateCellStyle: CellStyle , data: Any ) {
            setCellValue(cell, dateCellStyle, data, false);
        }

        fun setCellValue(cell: Cell, dateCellStyle: CellStyle, data: Any, wrap: Boolean) {
            if (data is Date) {
                cell.cellStyle = dateCellStyle
                cell.setCellValue(data)
            } else if (data is LocalDateTime) {
                cell.cellStyle = dateCellStyle
                cell.setCellValue(data)
            } else if (data is Double) {
                cell.setCellValue(data)
            } else if (data is String) {
                cell.setCellValue(data)
            } else if (data is Boolean) {
                cell.setCellValue(data)
            } else {
                if (wrap) {
                    cell.cellStyle = dateCellStyle
                }
                cell.setCellValue(data.toString())
            }
        }

        fun isEmptyRow(row: Row?): Boolean{
            if (row == null){
                return true
            }
            if (row.lastCellNum <= 0){
                return true
            }

            for (cellNum in  row.firstCellNum until row.lastCellNum){
                var cell = row.getCell(cellNum)
                if (cell != null && cell.cellType != CellType.BLANK && StringUtils.isNotBlank(cell.toString())){
                    return false
                }
            }

            return true
        }

        fun getCellValue(cell: Cell?, defaultValue: String?, maxLength: Long): String {
            if (cell != null){
                var value: String

                if(CellType.NUMERIC == cell.cellType){
                    value = if(DateUtil.isCellDateFormatted(cell)){
                        var date = cell.dateCellValue
                        SimpleDateFormat(DATE_FORMAT).format(date)
                    } else {
                        String.format("%.0f", cell.numericCellValue);
                    }
                } else {
                    value = try{
                        cell.stringCellValue
                    } catch (e: IllegalStateException) {
                        String.format("%.0f", cell.numericCellValue);
                    }
                }

                value = StringUtils.trim(value)
                if (StringUtils.isEmpty(value)){
                    if(defaultValue == null) {
                        throw BadRequestException("Empty Value")
                    }
                    value = defaultValue
                } else {
                    if (maxLength > 0 && value.length > maxLength){
                        throw BadRequestException("Max Length :  $maxLength")
                    }
                }

                return value
            }else{
                if (defaultValue == null){
                    throw  BadRequestException("Empty Value")
                }
                return defaultValue
            }
        }

        @Throws(IOException::class)
        fun writeXlsx(file: File, workbook: Workbook){
            write(file, workbook)
        }

        @Throws(IOException::class)
        private fun write(file: File, workbook: Workbook){
            val os: FileOutputStream = FileOutputStream(file)
            workbook.write(os)
        }

    }
}