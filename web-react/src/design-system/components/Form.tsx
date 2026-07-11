import type {
  InputHTMLAttributes,
  ReactNode,
  SelectHTMLAttributes,
  TextareaHTMLAttributes,
} from 'react'
import './Form.css'

export type FieldProps = InputHTMLAttributes<HTMLInputElement> & {
  error?: ReactNode
  hint?: ReactNode
  label: ReactNode
}

export function Field({ error, hint, label, ...inputProps }: FieldProps) {
  return (
    <label className="field">
      <span>{label}</span>
      <input {...inputProps} className="field__control field__control--input" />
      {error ? <span className="field__error">{error}</span> : null}
      {!error && hint ? <span className="field__hint">{hint}</span> : null}
    </label>
  )
}

export type SelectProps = SelectHTMLAttributes<HTMLSelectElement> & {
  error?: ReactNode
  hint?: ReactNode
  label: ReactNode
}

export function Select({ children, error, hint, label, ...selectProps }: SelectProps) {
  return (
    <label className="field">
      <span>{label}</span>
      <select {...selectProps} className="field__control field__control--select">
        {children}
      </select>
      {error ? <span className="field__error">{error}</span> : null}
      {!error && hint ? <span className="field__hint">{hint}</span> : null}
    </label>
  )
}

export type TextareaProps = TextareaHTMLAttributes<HTMLTextAreaElement> & {
  error?: ReactNode
  hint?: ReactNode
  label: ReactNode
}

export function Textarea({ error, hint, label, ...textareaProps }: TextareaProps) {
  return (
    <label className="field">
      <span>{label}</span>
      <textarea
        {...textareaProps}
        className="field__control field__control--textarea"
      />
      {error ? <span className="field__error">{error}</span> : null}
      {!error && hint ? <span className="field__hint">{hint}</span> : null}
    </label>
  )
}
