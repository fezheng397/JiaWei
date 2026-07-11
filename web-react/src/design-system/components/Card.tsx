import type { HTMLAttributes, ReactNode } from 'react'
import './Card.css'

export type CardProps = HTMLAttributes<HTMLDivElement> & {
  interactive?: boolean
  variant?: 'default' | 'empty'
}

export function Card({
  children,
  className,
  interactive = false,
  variant = 'default',
  ...props
}: CardProps) {
  return (
    <div
      {...props}
      className={[
        'card',
        interactive ? 'card--interactive' : null,
        variant === 'empty' ? 'card--empty' : null,
        className,
      ]
        .filter(Boolean)
        .join(' ')}
    >
      {children}
    </div>
  )
}

export function CardImage({ children }: { children: ReactNode }) {
  return <div className="card__image">{children}</div>
}

export function CardBody({ children }: { children: ReactNode }) {
  return <div className="card__body">{children}</div>
}

export function CardFooter({ children }: { children: ReactNode }) {
  return <div className="card__footer">{children}</div>
}

export function CardStats({ children }: { children: ReactNode }) {
  return <div className="card__stats">{children}</div>
}

export function CardStat({ children }: { children: ReactNode }) {
  return <span className="card__stat type-ui-sm">{children}</span>
}
