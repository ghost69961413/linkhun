import { useState, type FormEvent } from 'react';
import { Check, KeyRound, Mail } from 'lucide-react';
import { Button } from '../ui/Button';
import { useAuthStore } from '../../features/auth/authStore';
import { useChangeEmail } from '../../hooks/useChangeEmail';
import { getApiErrorMessage } from '../../utils/apiError';
import type { AuthSession } from '../../types/auth';

const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

export function AccountEmailSettings() {
  const user = useAuthStore(state => state.user);
  const rememberMe = useAuthStore(state => state.rememberMe);
  const setSession = useAuthStore(state => state.setSession);
  const [newEmail, setNewEmail] = useState('');
  const [password, setPassword] = useState('');
  const [validation, setValidation] = useState('');
  const changeEmail = useChangeEmail();

  function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const normalizedEmail = newEmail.trim().toLowerCase();
    if (!emailPattern.test(normalizedEmail)) { setValidation('Enter a valid email address.'); return; }
    if (normalizedEmail === user?.email.toLowerCase()) { setValidation('Enter an email address different from your current one.'); return; }
    if (!password) { setValidation('Enter your current password.'); return; }
    setValidation('');
    changeEmail.mutate({ newEmail: normalizedEmail, password }, {
      onSuccess: result => {
        const session: AuthSession = {
          accessToken: result.token,
          rememberMe,
          user: { ...user!, email: result.email, username: result.username },
        };
        setSession(session);
        setNewEmail('');
        setPassword('');
      },
    });
  }

  return <section className="surface-card settings-card account-email-card">
    <div className="settings-icon"><Mail size={18}/></div>
    <div className="account-email-heading"><small className="page-kicker">ACCOUNT</small><h2>Email Address</h2><p>Use this address to sign in and receive account updates.</p></div>
    <div className="account-current-email"><small>Current Email</small><strong>{user?.email || 'Not available'}</strong></div>
    <form className="account-email-form" onSubmit={submit} noValidate>
      <label htmlFor="change-email-new">New Email</label>
      <input id="change-email-new" type="email" autoComplete="email" value={newEmail} onChange={event => { setNewEmail(event.target.value); setValidation(''); changeEmail.reset(); }} placeholder="name@example.com"/>
      <label htmlFor="change-email-password">Current Password</label>
      <div className="account-password-field"><KeyRound size={15}/><input id="change-email-password" type="password" autoComplete="current-password" value={password} onChange={event => { setPassword(event.target.value); changeEmail.reset(); }} placeholder="Confirm your password"/></div>
      {(validation || changeEmail.isError) && <p className="field-error" role="alert">{validation || getApiErrorMessage(changeEmail.error, 'Email could not be updated. Please try again.')}</p>}
      {changeEmail.isSuccess && <p className="account-email-success" role="status"><Check size={15}/> Email address updated successfully.</p>}
      <Button type="submit" variant="primary" disabled={changeEmail.isPending}><Mail size={14}/>{changeEmail.isPending ? 'Updating…' : 'Update Email'}</Button>
    </form>
  </section>;
}
