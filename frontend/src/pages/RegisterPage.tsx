import { useState, type FormEvent } from 'react';
import { Link, Navigate, useNavigate } from 'react-router-dom';
import { useMutation } from '@tanstack/react-query';
import { ArrowRight, Check, Code2, Eye, EyeOff, LoaderCircle, Mail, UserRound } from 'lucide-react';
import { Button } from '../components/ui/Button';
import { useAuthStore } from '../features/auth/authStore';
import { authService } from '../services/authService';
import { getApiErrorMessage } from '../utils/apiError';
import { getPasswordStrength, validateRegister, type RegisterField, type ValidationErrors } from '../utils/authValidation';
import type { RegisterInput } from '../types/auth';
import { PROFILE_TYPES } from '../types/profile';

const initialValues: RegisterInput = { fullName: '', username: '', email: '', password: '', confirmPassword: '', profileType: 'PROFESSIONAL' };

export function RegisterPage() {
  const [values, setValues] = useState<RegisterInput>(initialValues);
  const [errors, setErrors] = useState<ValidationErrors<RegisterField>>({});
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmation, setShowConfirmation] = useState(false);
  const setSession = useAuthStore((state) => state.setSession);
  const enterDemo = useAuthStore((state) => state.enterDemo);
  const isAuthenticated = useAuthStore((state) => Boolean(state.accessToken));
  const navigate = useNavigate();
  const register = useMutation({
    mutationFn: () => authService.register(values),
    onSuccess: (session) => { setSession(session); navigate('/home', { replace: true }); },
  });
  const strength = getPasswordStrength(values.password);

  if (isAuthenticated) return <Navigate to="/home" replace />;

  function updateField(field: RegisterField, value: string) {
    setValues((current) => ({ ...current, [field]: value }));
    setErrors((current) => ({ ...current, [field]: undefined }));
    register.reset();
  }

  function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const validation = validateRegister(values);
    setErrors(validation);
    if (Object.keys(validation).length > 0) return;
    register.mutate();
  }

  function errorFor(field: RegisterField) {
    const error = errors[field];
    return error ? <span className="field-error" id={`register-${field}-error`}>{error}</span> : null;
  }

  return (
    <main className="auth-screen register-screen">
      <section className="auth-form-side">
        <div className="auth-top"><Link to="/register" className="workspace-brand"><span className="brand-glyph"><Code2 size={18} /></span><span>linkhub</span></Link></div>
        <div className="auth-form-wrap">
          <div className="auth-intro"><span className="auth-kicker"><span className="pulse" /> MAKE YOURSELF AT HOME</span><h1>Build your<br /><em>corner of the internet.</em></h1><p>Bring your work, your curiosity, and your next collaboration together.</p></div>
          <form className="auth-form" onSubmit={submit} noValidate>
            <label htmlFor="register-full-name">Full name</label>
            <div className={`input-shell ${errors.fullName ? 'input-invalid' : ''}`}><UserRound size={16} aria-hidden="true" /><input id="register-full-name" autoComplete="name" required maxLength={100} value={values.fullName} onChange={(event) => updateField('fullName', event.target.value)} aria-invalid={Boolean(errors.fullName)} aria-describedby={errors.fullName ? 'register-fullName-error' : undefined} placeholder="Sarah Chen" /></div>
            {errorFor('fullName')}

            <label htmlFor="register-username">Username</label>
            <div className={`input-shell ${errors.username ? 'input-invalid' : ''}`}><span className="input-prefix">@</span><input id="register-username" autoComplete="username" required minLength={3} maxLength={30} value={values.username} onChange={(event) => updateField('username', event.target.value)} aria-invalid={Boolean(errors.username)} aria-describedby={errors.username ? 'register-username-error' : 'username-hint'} placeholder="sarahbuilds" /></div>
            {errors.username ? errorFor('username') : <span className="field-hint" id="username-hint">3–30 letters, numbers, dots, underscores, or hyphens.</span>}

            <label htmlFor="register-email">Email address</label>
            <div className={`input-shell ${errors.email ? 'input-invalid' : ''}`}><Mail size={16} aria-hidden="true" /><input id="register-email" autoComplete="email" inputMode="email" type="email" required value={values.email} onChange={(event) => updateField('email', event.target.value)} aria-invalid={Boolean(errors.email)} aria-describedby={errors.email ? 'register-email-error' : undefined} placeholder="you@example.com" /></div>
            {errorFor('email')}

            <label htmlFor="register-password">Password</label>
            <div className={`input-shell password-shell ${errors.password ? 'input-invalid' : ''}`}><input id="register-password" autoComplete="new-password" required type={showPassword ? 'text' : 'password'} value={values.password} onChange={(event) => updateField('password', event.target.value)} aria-invalid={Boolean(errors.password)} aria-describedby={errors.password ? 'register-password-error' : 'password-strength'} placeholder="Create a strong password" /><button type="button" className="reveal-button" aria-label={showPassword ? 'Hide password' : 'Show password'} onClick={() => setShowPassword((visible) => !visible)}>{showPassword ? <EyeOff size={15} /> : <Eye size={15} />}</button></div>
            <div className="strength-meter" id="password-strength" role="progressbar" aria-label="Password strength" aria-valuemin={0} aria-valuemax={5} aria-valuenow={strength.score}><div className="strength-bars">{Array.from({ length: 5 }, (_, index) => <span key={index} className={index < strength.score ? `filled strength-color-${strength.score}` : ''} />)}</div><b>{strength.label}</b></div>
            {errors.password ? errorFor('password') : <span className="field-hint">{strength.hint}</span>}

            <label htmlFor="register-confirm-password">Confirm password</label>
            <div className={`input-shell password-shell ${errors.confirmPassword ? 'input-invalid' : ''}`}><input id="register-confirm-password" autoComplete="new-password" required type={showConfirmation ? 'text' : 'password'} value={values.confirmPassword} onChange={(event) => updateField('confirmPassword', event.target.value)} aria-invalid={Boolean(errors.confirmPassword)} aria-describedby={errors.confirmPassword ? 'register-confirmPassword-error' : undefined} placeholder="Enter your password again" /><button type="button" className="reveal-button" aria-label={showConfirmation ? 'Hide confirmation password' : 'Show confirmation password'} onClick={() => setShowConfirmation((visible) => !visible)}>{showConfirmation ? <EyeOff size={15} /> : <Eye size={15} />}</button></div>
            {values.confirmPassword && values.password === values.confirmPassword && !errors.confirmPassword && <span className="field-success"><Check size={13} /> Passwords match</span>}
            {errorFor('confirmPassword')}

            <label htmlFor="register-profile-type">Professional profile type</label>
            <div className="input-shell"><select id="register-profile-type" value={values.profileType} onChange={event => updateField('profileType', event.target.value as RegisterInput['profileType'])}>{PROFILE_TYPES.map(type => <option key={type} value={type}>{type}</option>)}</select></div>

            {register.isError && <div className="form-error" role="alert">{getApiErrorMessage(register.error, 'Account creation failed. Please check your details and try again.')}</div>}
            <Button type="submit" variant="primary" className="auth-submit" disabled={register.isPending} aria-live="polite">{register.isPending && <LoaderCircle className="spin" size={16} aria-hidden="true" />}{register.isPending ? 'Creating account…' : 'Create account'}<ArrowRight size={15} /></Button>
            {import.meta.env.DEV && <button type="button" className="inline-demo" onClick={() => { enterDemo(); navigate('/home', { replace: true }); }}>Or explore the demo first</button>}
          </form>
          <div className="auth-footer">Already have an account? <Link to="/login">Sign in <ArrowRight size={13} /></Link></div>
        </div>
        <div className="auth-legal">© 2026 LinkHub <span>·</span> Built for people who build things</div>
      </section>
      <aside className="register-art" aria-label="Join the LinkHub developer community"><div className="register-art-orb" /><div className="register-art-card"><div className="register-art-mark"><Code2 /></div><div className="register-art-spark">✦</div><h2>Find your<br /><em>people.</em></h2><p>Show what you’re building. Learn what others are making. Make something better together.</p><div className="register-stat-row"><span><b>24k+</b><small>developers</small></span><span><b>8.2k</b><small>projects shared</small></span><span><b>1.4k</b><small>teams formed</small></span></div></div></aside>
    </main>
  );
}
