import { useState, type FormEvent } from 'react';
import { Link, Navigate, useLocation, useNavigate } from 'react-router-dom';
import { useMutation } from '@tanstack/react-query';
import { ArrowRight, Code2, LoaderCircle, LockKeyhole, Mail, Sparkles } from 'lucide-react';
import { Button } from '../components/ui/Button';
import { GitHubIcon } from '../components/GitHubIcon';
import { useAuthStore } from '../features/auth/authStore';
import { retryTransientRequest } from '../services/queryClient';
import { useThemeStore } from '../features/theme/themeStore';
import { authService } from '../services/authService';
import { getApiErrorMessage } from '../utils/apiError';
import { validateLogin, type LoginField, type ValidationErrors } from '../utils/authValidation';

export function LoginPage() {
  const [usernameOrEmail, setUsernameOrEmail] = useState('');
  const [password, setPassword] = useState('');
  const [rememberMe, setRememberMe] = useState(true);
  const [errors, setErrors] = useState<ValidationErrors<LoginField>>({});
  const setSession = useAuthStore((state) => state.setSession);
  const enterDemo = useAuthStore((state) => state.enterDemo);
  const isAuthenticated = useAuthStore((state) => Boolean(state.accessToken));
  const theme = useThemeStore((state) => state.theme);
  const toggleTheme = useThemeStore((state) => state.toggleTheme);
  const navigate = useNavigate();
  const location = useLocation();
  const destination = (location.state as { from?: { pathname?: string } } | null)?.from?.pathname ?? '/home';

  const login = useMutation({
    mutationFn: () => authService.login({ usernameOrEmail, password, rememberMe }),
    retry: retryTransientRequest,
    retryDelay: 2_000,
    onSuccess: (session) => {
      setSession(session);
      navigate(destination.startsWith('/') ? destination : '/home', { replace: true });
    },
  });

  if (isAuthenticated) return <Navigate to="/home" replace />;

  function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const validation = validateLogin({ usernameOrEmail, password });
    setErrors(validation);
    if (Object.keys(validation).length > 0) return;
    login.mutate();
  }

  function updateIdentifier(value: string) {
    setUsernameOrEmail(value);
    setErrors((current) => ({ ...current, usernameOrEmail: undefined }));
    login.reset();
  }
  function updatePassword(value: string) {
    setPassword(value);
    setErrors((current) => ({ ...current, password: undefined }));
    login.reset();
  }

  return (
    <main className="auth-screen">
      <section className="auth-form-side">
        <div className="auth-top">
          <Link to="/login" className="workspace-brand"><span className="brand-glyph"><Code2 size={18} /></span><span>linkhub</span></Link>
          <button className="theme-mini" type="button" onClick={toggleTheme}>{theme === 'dark' ? 'Light' : 'Dark'} mode</button>
        </div>
        <div className="auth-form-wrap">
          <div className="auth-intro">
            <span className="auth-kicker"><span className="pulse" /> YOUR DEVELOPER NETWORK</span>
            <h1>Welcome back<br /><em>to your people.</em></h1>
            <p>Pick up where you left off. Your next opportunity might be one connection away.</p>
          </div>
          <form className="auth-form" onSubmit={submit} noValidate>
            <label htmlFor="login-identifier">Username or Email</label>
            <div className={`input-shell ${errors.usernameOrEmail ? 'input-invalid' : ''}`}>
              <Mail size={16} aria-hidden="true" />
              <input id="login-identifier" autoComplete="username" type="text" required value={usernameOrEmail}
                onChange={(event) => updateIdentifier(event.target.value)} aria-invalid={Boolean(errors.usernameOrEmail)} aria-describedby={errors.usernameOrEmail ? 'login-identifier-error' : undefined} placeholder="username or you@example.com" />
            </div>
            {errors.usernameOrEmail && <span className="field-error" id="login-identifier-error">{errors.usernameOrEmail}</span>}

            <label htmlFor="login-password">Password</label>
            <div className={`input-shell ${errors.password ? 'input-invalid' : ''}`}>
              <LockKeyhole size={16} aria-hidden="true" />
              <input id="login-password" autoComplete="current-password" type="password" required value={password}
                onChange={(event) => updatePassword(event.target.value)} aria-invalid={Boolean(errors.password)} aria-describedby={errors.password ? 'login-password-error' : undefined} placeholder="Enter your password" />
            </div>
            {errors.password && <span className="field-error" id="login-password-error">{errors.password}</span>}

            <div className="auth-options">
              <label className="remember"><input type="checkbox" checked={rememberMe} onChange={(event) => setRememberMe(event.target.checked)} /> Remember me</label>
            </div>
            {login.isError && <div className="form-error" role="alert">{getApiErrorMessage(login.error, 'We couldn’t sign you in. Please try again.')}</div>}
            <Button type="submit" variant="primary" className="auth-submit" disabled={login.isPending} aria-live="polite">
              {login.isPending && <LoaderCircle className="spin" size={16} aria-hidden="true" />}
              {login.isPending ? 'Signing in…' : 'Sign in'} <ArrowRight size={15} />
            </Button>
            <div className="or-divider"><span />OR<span /></div>
            {import.meta.env.DEV && <button type="button" className="github-auth" onClick={() => { enterDemo(); navigate('/home', { replace: true }); }}><GitHubIcon size={16} /> Explore demo workspace</button>}
            {import.meta.env.DEV && <p className="demo-note"><Sparkles size={13} /> Demo opens with sample data. Sign-in uses the configured API.</p>}
          </form>
          <div className="auth-footer">New to LinkHub? <Link to="/register">Create an account <ArrowRight size={13} /></Link></div>
        </div>
        <div className="auth-legal">© 2026 LinkHub <span>·</span> Built for people who build things</div>
      </section>
      <aside className="auth-art-side" aria-label="LinkHub community">
        <div className="auth-art-grid" /><div className="art-floating art-window"><div className="window-dots"><i /><i /><i /></div>
          <div className="code-line purple-line">const <b>builder</b> = {'{'}</div><div className="code-line indent">name: <strong>"you"</strong>,</div><div className="code-line indent">work: <strong>"in progress"</strong>,</div><div className="code-line indent">people: <strong>"your kind"</strong></div><div className="code-line">{'}'};</div><div className="code-cursor" /></div>
        <div className="art-orbit art-orbit-a" /><div className="art-orbit art-orbit-b" />
        <div className="art-caption"><div className="caption-icon"><Sparkles size={17} /></div><h2>What you build<br />is only <em>part</em> of your story.</h2><p>Meet the people who make the journey better.</p><div className="caption-avatars"><span>SC</span><span>JM</span><span>AK</span><i>+2.4k builders</i></div></div>
        <div className="art-footnote"><span className="pulse" /> A little more human. A lot more you.</div>
      </aside>
    </main>
  );
}
