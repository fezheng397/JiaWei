import type { ReactNode } from 'react'
import './Shell.css'

export type ShellNavItem = {
  href: string
  isCurrent?: boolean
  label: string
}

export type ShellProps = {
  children: ReactNode
  homeHref?: string
  navItems?: ShellNavItem[]
}

export function Shell({ children, homeHref = '/', navItems = [] }: ShellProps) {
  return (
    <div className="app-shell">
      <header className="app-header">
        <div className="app-header-inner">
          <a className="app-brand app-logo" href={homeHref}>
            Jiawei
          </a>
          {navItems.length > 0 ? (
            <nav aria-label="Primary" className="app-header-nav">
              {navItems.map((item) => (
                <a
                  aria-current={item.isCurrent ? 'page' : undefined}
                  href={item.href}
                  key={item.href}
                >
                  {item.label}
                </a>
              ))}
            </nav>
          ) : null}
        </div>
      </header>
      <main className="app-main">{children}</main>
    </div>
  )
}
