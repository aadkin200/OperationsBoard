import { Component, EventEmitter, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-blocker-modal',
  imports: [FormsModule],
  templateUrl: './blocker-modal.html',
  styleUrl: './blocker-modal.scss',
})
export class BlockerModal {
  reason = '';

  @Output() cancel = new EventEmitter<void>();
  @Output() submitReason = new EventEmitter<string>();

  submit(): void {
    const trimmed = this.reason.trim();

    if (!trimmed) {
      return;
    }

    this.submitReason.emit(trimmed);
    this.reason = '';
  }

  close(): void {
    this.reason = '';
    this.cancel.emit();
  }
}
