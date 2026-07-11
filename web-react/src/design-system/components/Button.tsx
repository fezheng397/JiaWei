import type { AnchorHTMLAttributes, ButtonHTMLAttributes, ReactNode } from 'react'
import './Button.css'

export type ButtonVariant = 'unstyled' | 'primary' | 'secondary' | 'ghost' | 'icon'
export type ButtonSize = 'sm' | 'md' | 'lg'

type ButtonBaseProps = {
  children?: ReactNode
  className?: string
  fullWidth?: boolean
  loading?: boolean
  size?: ButtonSize
  variant?: ButtonVariant
}

type ButtonAsAnchorProps = ButtonBaseProps &
  AnchorHTMLAttributes<HTMLAnchorElement> & {
    disabled?: boolean
    href: string
  }

type ButtonAsButtonProps = ButtonBaseProps &
  ButtonHTMLAttributes<HTMLButtonElement> & {
    href?: never
  }

export type ButtonProps = ButtonAsAnchorProps | ButtonAsButtonProps

function getButtonClassName({
  className,
  fullWidth,
  size,
  variant,
}: Required<Pick<ButtonBaseProps, 'fullWidth' | 'size' | 'variant'>> &
  Pick<ButtonBaseProps, 'className'>) {
  return [
    'button-control',
    `button-control--${variant}`,
    variant === 'unstyled' ? null : `button-control--${size}`,
    fullWidth ? 'button-control--full-width' : null,
    className,
  ]
    .filter(Boolean)
    .join(' ')
}

function isAnchorButton(props: ButtonProps): props is ButtonAsAnchorProps {
  return typeof (props as { href?: unknown }).href === 'string'
}

export function Button(props: ButtonProps) {
  const fullWidth = props.fullWidth ?? false
  const loading = props.loading ?? false
  const size = props.size ?? 'md'
  const variant = props.variant ?? 'unstyled'
  const buttonClassName = getButtonClassName({
    className: props.className,
    fullWidth,
    size,
    variant,
  })

  if (isAnchorButton(props)) {
    const {
      children,
      className: _className,
      disabled,
      fullWidth: _fullWidth,
      href,
      loading: _loading,
      onClick,
      rel,
      size: _size,
      target,
      variant: _variant,
      ...anchorProps
    } = props
    const isUnavailable = Boolean(disabled || loading)
    const resolvedRel = rel ?? (target === '_blank' ? 'noreferrer noopener' : undefined)

    return (
      <a
        {...anchorProps}
        aria-busy={loading || undefined}
        aria-disabled={isUnavailable || undefined}
        className={buttonClassName}
        href={href}
        onClick={(event) => {
          if (isUnavailable) {
            event.preventDefault()
            event.stopPropagation()
            return
          }
          onClick?.(event)
        }}
        rel={resolvedRel}
        tabIndex={isUnavailable ? -1 : anchorProps.tabIndex}
        target={target}
      >
        {children}
      </a>
    )
  }

  const {
    children,
    className: _className,
    disabled,
    fullWidth: _fullWidth,
    loading: _loading,
    size: _size,
    type = 'button',
    variant: _variant,
    ...buttonProps
  } = props
  const isUnavailable = Boolean(disabled || loading)

  return (
    <button
      {...buttonProps}
      aria-busy={loading || undefined}
      className={buttonClassName}
      disabled={isUnavailable}
      type={type}
    >
      {children}
    </button>
  )
}
