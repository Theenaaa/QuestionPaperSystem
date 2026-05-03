import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Question } from '../models/question';

@Injectable({
  providedIn: 'root'
})
export class QuestionService {

  private api = 'http://localhost:8080/questions';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Question[]> {
    return this.http.get<Question[]>(this.api);
  }

  searchQuestions(subject: string, difficulty: string): Observable<Question[]> {
    return this.http.get<Question[]>(`${this.api}/search?subject=${subject}&difficulty=${difficulty}`);
  }

  getPaginated(page: number, size: number): Observable<any> {
    return this.http.get<any>(`${this.api}/page?page=${page}&size=${size}`);
  }

  add(question: Question): Observable<Question> {
    return this.http.post<Question>(this.api, question);
  }

  deleteQuestion(id: number) {
    return this.http.delete(`${this.api}/${id}`, { responseType: 'text' });
  }

  updateQuestion(id: number, question: any) {
    return this.http.put(`${this.api}/${id}`, question);
  }

  generateSmartPaper(subject: string) {
    return this.http.get<any>(`${this.api}/smart?subject=${subject}`);
  }

  generateFlexible(subject: string, totalQuestions: number, easy?: number, medium?: number, hard?: number) {
    return this.http.get<any>(
      `${this.api}/generate-flexible?subject=${subject}&totalQuestions=${totalQuestions}&easy=${easy || ''}&medium=${medium || ''}&hard=${hard || ''}`
    );
  }

  generateByMarks(subject: string, totalMarks: number) {
    return this.http.get<any>(
      `${this.api}/generate-by-marks?subject=${subject}&totalMarks=${totalMarks}`
    );
  }

  generateSelected(ids: number[]) {
    return this.http.post<any>(`${this.api}/generate-selected`, ids);
  }

  downloadPdf(subject: string) {
    return this.http.get(`${this.api}/export?subject=${subject}`, { responseType: 'blob' });
  }

  downloadPdfFromIds(ids: number[]) {
    return this.http.post(`${this.api}/export-selected`, ids, { responseType: 'blob' });
  }

  exportSelectedPdf(ids: number[]) {
    return this.http.post(
      `${this.api}/export-selected`,
      ids,
      { responseType: 'blob' }
    );
  }
}
