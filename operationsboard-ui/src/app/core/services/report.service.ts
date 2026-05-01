import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ReportService {
  private readonly baseUrl = 'http://localhost:8080/api/reports';

  constructor(private http: HttpClient) {}

  downloadExcelReport(): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/executive/excel`, { responseType: 'blob' });
  }

  downloadPdfReport(): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/executive/pdf`, { responseType: 'blob' });
  }

  triggerDownload(blob: Blob, filename: string): void {
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = filename;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    window.URL.revokeObjectURL(url);
  }
}
