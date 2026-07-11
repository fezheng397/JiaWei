import type { HTMLAttributes } from 'react'
import './TagBadge.css'

export function Tag({ children, className, ...props }: HTMLAttributes<HTMLSpanElement>) {
  return (
    <span {...props} className={['tag', className].filter(Boolean).join(' ')}>
      {children}
    </span>
  )
}

export type BadgeVariant = 'default' | 'success' | 'info' | 'danger'

export type BadgeProps = HTMLAttributes<HTMLSpanElement> & {
  variant?: BadgeVariant
}

export function Badge({
  children,
  className,
  variant = 'default',
  ...props
}: BadgeProps) {
  return (
    <span
      {...props}
      className={['badge', `badge--${variant}`, className].filter(Boolean).join(' ')}
    >
      {children}
    </span>
  )
}
