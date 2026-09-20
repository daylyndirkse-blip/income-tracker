package com.example.incometracker.util

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.example.incometracker.data.IncomeRecord
import java.io.File
import java.time.LocalDate

object CsvExporter {
    
    fun exportToCsv(context: Context, records: List<IncomeRecord>): File {
        val fileName = "income_export_${System.currentTimeMillis()}.csv"
        val file = File(context.cacheDir, fileName)
        
        file.bufferedWriter().use { writer ->
            // Header
            writer.write("Date,Source,Amount,Notes\n")
            
            // Data rows
            records.sortedByDescending { it.dateEpochDay }.forEach { record ->
                val date = LocalDate.ofEpochDay(record.dateEpochDay)
                val amount = formatCents(record.amountCents)
                val notes = record.notes.replace(",", ";").replace("\n", " ")
                writer.write("$date,${record.source},$amount,\"$notes\"\n")
            }
        }
        
        return file
    }
    
    fun shareFile(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Income Export")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        context.startActivity(Intent.createChooser(intent, "Export Income Data"))
    }
}
