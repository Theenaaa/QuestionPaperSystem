import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { QuestionService } from '../../services/question.service';
import { Question } from '../../models/question';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule], // ✅ Required for ngIf, ngFor, and ngModel
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {

  questions: Question[] = [];
  loading = true;

  newQuestion: Question = {
    text: '',
    subject: '',
    difficulty: '',
    topic: '',
    marks: 5
  };

  editing = false;
  editId: number | undefined = undefined;

  subject: string = '';
  paper: any;
  
  totalQuestions = 5;
  easy?: number;
  medium?: number;
  hard?: number;
  totalMarks?: number;
  generatedQuestions: any[] = [];
  selectedIds: number[] = [];
  
  searchSubject: string = '';
  searchDifficulty: string = '';

  errorMessage: string = '';
  successMessage: string = '';

  role = localStorage.getItem('role');

  activeTab: string = 'generate';

  constructor(
    private auth: AuthService, 
    private router: Router,
    private service: QuestionService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    console.log('DashboardComponent initialized');
    if (this.role === 'ADMIN') {
      this.activeTab = 'create';
    } else {
      this.activeTab = 'generate';
    }
    this.loadQuestions();
  }

  setTab(tab: string) {
    this.activeTab = tab;
    this.clearMessages();
  }

  loadQuestions() {
    this.loading = true;
    this.service.getAll().subscribe({
      next: (res) => {
        this.questions = res;
        this.loading = false;
        this.cdr.detectChanges(); // Force UI update
      },
      error: (err) => {
        console.error('API Error:', err);
        this.errorMessage = 'Failed to load questions from server.';
        this.loading = false;
        this.cdr.detectChanges(); // Force UI update
      }
    });
  }

  clearMessages() {
    this.errorMessage = '';
    this.successMessage = '';
  }

  addQuestion() {
    this.clearMessages();
    if (this.newQuestion.marks <= 0) {
      this.errorMessage = 'Marks must be greater than 0!';
      return;
    }

    this.service.add(this.newQuestion).subscribe({
      next: () => {
        this.successMessage = "Question added successfully!";
        this.resetForm();
        this.loadQuestions(); // Refresh list
      },
      error: (err) => {
        console.error('Error adding question', err);
        this.errorMessage = 'Failed to add question!';
      }
    });
  }

  editQuestion(q: Question) {
    this.clearMessages();
    this.editing = true;
    this.editId = q.id;

    // Load data into form (copy to avoid mutating the list directly)
    this.newQuestion = { ...q };
    
    // Switch to create tab to edit
    this.activeTab = 'create';
  }

  updateQuestion() {
    this.clearMessages();
    if (!this.editId) return;

    if (this.newQuestion.marks <= 0) {
      this.errorMessage = 'Marks must be greater than 0!';
      return;
    }

    this.service.updateQuestion(this.editId, this.newQuestion).subscribe({
      next: () => {
        this.successMessage = "Question updated successfully!";
        this.resetForm();
        this.loadQuestions();
      },
      error: (err) => {
        console.error('Error updating question', err);
        this.errorMessage = "Failed to update question!";
      }
    });
  }

  resetForm() {
    this.editing = false;
    this.editId = undefined;
    this.newQuestion = { text: '', subject: '', difficulty: '', topic: '', marks: 5 };
  }

  search() {
    this.clearMessages();
    this.loading = true;
    this.service.searchQuestions(this.searchSubject, this.searchDifficulty).subscribe({
      next: (res) => {
        this.questions = res;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error searching questions', err);
        this.errorMessage = 'Failed to search questions. Check console.';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  resetSearch() {
    this.searchSubject = '';
    this.searchDifficulty = '';
    this.loadQuestions();
  }

  deleteQuestion(id: number | undefined) {
    if (!id) return;
    if (!confirm("Are you sure you want to delete this question?")) return;
    this.clearMessages();
    this.service.deleteQuestion(id).subscribe({
      next: () => {
        alert("Deleted successfully ✅");
        this.successMessage = "Question deleted!";
        // Best Practice: Instantly update UI without making another API call
        this.questions = this.questions.filter(q => q.id !== id);
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error deleting question', err);
        this.errorMessage = "Failed to delete question!";
        this.cdr.detectChanges();
      }
    });
  }

  generatePaper() {
    this.clearMessages();
    if (!this.subject.trim()) {
      this.errorMessage = 'Please enter a subject first!';
      return;
    }
    this.service.generateSmartPaper(this.subject).subscribe({
      next: (data) => {
        this.paper = data;
        this.successMessage = "Paper generated successfully!";
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error generating paper', err);
        this.errorMessage = 'Failed to generate smart paper.';
      }
    });
  }

  generateFlexible() {
    this.clearMessages();
    if (!this.subject.trim()) {
      this.errorMessage = 'Please enter a subject first!';
      return;
    }
    this.service.generateFlexible(
      this.subject,
      this.totalQuestions,
      this.easy,
      this.medium,
      this.hard
    ).subscribe({
      next: (res: any) => {
        this.generatedQuestions = res;
        this.successMessage = "Flexible paper generated successfully!";
        this.cdr.detectChanges();
      },
      error: (err: any) => {
        console.error(err);
        this.errorMessage = 'Failed to generate flexible paper.';
      }
    });
  }

  generateByMarks() {
    this.clearMessages();
    if (!this.subject.trim() || !this.totalMarks) {
      this.errorMessage = 'Please enter a subject and total marks!';
      return;
    }
    this.service.generateByMarks(
      this.subject,
      this.totalMarks!
    ).subscribe({
      next: (res: any) => {
        this.generatedQuestions = res;
        this.successMessage = "Paper generated by marks successfully!";
        this.cdr.detectChanges();
      },
      error: (err: any) => {
        console.error(err);
        this.errorMessage = 'Failed to generate paper by marks.';
      }
    });
  }

  onSelect(id: number | undefined, event: any) {
    if (!id) return;
    if (event.target.checked) {
      this.selectedIds.push(id);
    } else {
      this.selectedIds = this.selectedIds.filter(i => i !== id);
    }
  }

  generateSelected() {
    this.clearMessages();
    if (this.selectedIds.length === 0) {
      this.errorMessage = 'Please select at least one question!';
      return;
    }
    this.service.generateSelected(this.selectedIds).subscribe({
      next: (res: any) => {
        this.generatedQuestions = res;
        this.successMessage = "Manual paper generated successfully!";
        this.cdr.detectChanges();
      },
      error: (err: any) => {
        console.error(err);
        this.errorMessage = 'Failed to generate manual paper.';
      }
    });
  }

  downloadPdf() {
    this.clearMessages();

    // If we have generated questions on the screen (from ANY method: Flexible, Marks, or Manual)
    // We send those exact IDs to the backend so the PDF matches the screen perfectly!
    if (this.generatedQuestions.length > 0) {
      const ids = this.generatedQuestions.map(q => q.id);
      this.service.downloadPdfFromIds(ids).subscribe({
        next: (blob: Blob) => {
          const url = window.URL.createObjectURL(blob);
          const link = document.createElement('a');
          link.href = url;
          link.download = 'Generated_Question_Paper.pdf';
          link.click();
        },
        error: (err: any) => {
          console.error('Error downloading PDF', err);
          this.errorMessage = 'Failed to download PDF. Please ensure /export-selected is implemented in your Java backend!';
        }
      });
      return;
    }

    // Fallback to original logic if no questions are manually generated yet
    if (!this.subject.trim()) {
      this.errorMessage = 'Please generate a paper or enter a subject first!';
      return;
    }
    this.service.downloadPdf(this.subject).subscribe({
      next: (blob: Blob) => {
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = this.subject + '_Paper.pdf';
        link.click();
      },
      error: (err: any) => {
        console.error('Error downloading PDF', err);
        this.errorMessage = 'Failed to download PDF.';
      }
    });
  }

  downloadSelectedPdf() {
    this.clearMessages();
    if (this.selectedIds.length === 0) {
      this.errorMessage = 'Please select at least one question!';
      return;
    }
    this.service.exportSelectedPdf(this.selectedIds)
      .subscribe({
        next: (blob: Blob) => {
          const url = window.URL.createObjectURL(blob);
          const a = document.createElement('a');
          a.href = url;
          a.download = 'Custom_Paper.pdf';
          a.click();
          window.URL.revokeObjectURL(url);
        },
        error: (err: any) => {
          console.error(err);
          this.errorMessage = 'Failed to download selected PDF. Ensure /export-selected exists on backend!';
        }
      });
  }

  logout() {
    this.auth.logout();
    this.router.navigate(['/']);
  }
}
