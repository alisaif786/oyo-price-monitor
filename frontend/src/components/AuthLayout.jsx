import { Link } from 'react-router-dom'

export default function AuthLayout({ eyebrow, title, description, children, footer }) {
  return (
    <main className="auth-page">
      <section className="auth-intro">
        <Link className="brand" to="/" aria-label="OYO Price Monitor home">
          <span className="brand-mark" aria-hidden="true">O</span>
          <span>OYO <strong>Price Monitor</strong></span>
        </Link>
        <div className="auth-intro-copy">
          <span className="eyebrow">SMARTER HOTEL STAYS</span>
          <h1>Find the right stay at the right price.</h1>
          <p>Keep your hotel plans in one place and track prices as they change.</p>
        </div>
        <div className="auth-intro-note">
          <span className="note-icon" aria-hidden="true">↘</span>
          <span>Know when your hotel price is worth booking.</span>
        </div>
      </section>

      <section className="auth-main">
        <div className="auth-card">
          <div className="auth-card-heading">
            <span className="eyebrow">{eyebrow}</span>
            <h2>{title}</h2>
            <p>{description}</p>
          </div>
          {children}
          <div className="auth-footer">{footer}</div>
        </div>
      </section>
    </main>
  )
}
