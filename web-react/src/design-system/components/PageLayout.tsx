import type { ReactNode } from 'react'
import './PageLayout.css'

export type PageLayoutProps = {
  action?: ReactNode
  children: ReactNode
  description?: ReactNode
  title?: ReactNode
  variant?: 'standard' | 'document'
}

export function PageLayout({
  action,
  children,
  description,
  title,
  variant = 'standard',
}: PageLayoutProps) {
  return (
    <section
      className={[
        'page-layout',
        variant === 'document' ? 'page-layout--document' : null,
      ]
        .filter(Boolean)
        .join(' ')}
    >
      {title ? (
        <div
          className={[
            'page-layout__heading',
            action ? 'page-layout__heading--with-action' : null,
          ]
            .filter(Boolean)
            .join(' ')}
        >
          <div>
            <h1 className="page-layout__title type-page-title">{title}</h1>
            {description ? (
              <p className="page-layout__description type-body">{description}</p>
            ) : null}
          </div>
          {action}
        </div>
      ) : null}
      {children}
    </section>
  )
}
