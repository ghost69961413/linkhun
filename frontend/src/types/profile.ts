export interface ProfileExperience {
  id?: number;
  company: string;
  title: string;
  employmentType: string;
  location: string;
  startYear: string;
  endYear: string;
  current: boolean;
  description: string;
}

export interface ProfileEducation {
  id?: number;
  school: string;
  degree: string;
  fieldOfStudy: string;
  startYear: string;
  endYear: string;
  grade: string;
  description: string;
}

export interface ProfileCertification {
  id?: number;
  name: string;
  issuer: string;
  issueDate: string;
  credentialUrl: string;
}

export interface Profile {
  id?: number;
  userId?: number;
  username: string;
  fullName: string;
  firstName?: string;
  lastName?: string;
  headline: string;
  location: string;
  bio: string;
  profilePicture: string;
  coverImage: string;
  skills: string[];
  experience: ProfileExperience[];
  education: ProfileEducation[];
  certifications: ProfileCertification[];
  github: string;
  linkedin: string;
  portfolio: string;
  website?: string;
  githubUrl?: string;
  linkedinUrl?: string;
  portfolioUrl?: string;
  profileType: ProfileType;
  roleDetails: Record<string, string | string[]>;
  followers?: number;
  following?: number;
}

export type ProfileType = 'STUDENT' | 'TEACHER' | 'DEVELOPER' | 'HR' | 'RECRUITER' | 'CEO' | 'FOUNDER' | 'PROFESSIONAL';
export const PROFILE_TYPES: ProfileType[] = ['STUDENT', 'TEACHER', 'DEVELOPER', 'HR', 'RECRUITER', 'CEO', 'FOUNDER', 'PROFESSIONAL'];
export const ROLE_FIELDS: Record<ProfileType, { key: string; label: string }[]> = {
  STUDENT: [{key:'college',label:'College'},{key:'degree',label:'Degree'},{key:'branch',label:'Branch'},{key:'graduationYear',label:'Graduation year'},{key:'cgpa',label:'CGPA'},{key:'projects',label:'Projects'},{key:'certifications',label:'Certifications'},{key:'github',label:'GitHub'}],
  TEACHER: [{key:'institution',label:'Institution'},{key:'department',label:'Department'},{key:'designation',label:'Designation'},{key:'subjects',label:'Subjects'},{key:'researchInterests',label:'Research interests'},{key:'publications',label:'Publications'},{key:'experience',label:'Experience'}],
  DEVELOPER: [{key:'programmingLanguages',label:'Programming languages'},{key:'frameworks',label:'Frameworks'},{key:'github',label:'GitHub'},{key:'repositories',label:'Repositories'},{key:'projects',label:'Projects'},{key:'experience',label:'Experience'},{key:'developerScore',label:'Developer score'}],
  HR: [{key:'company',label:'Company'},{key:'designation',label:'Designation'},{key:'hiringFor',label:'Hiring for'},{key:'experience',label:'Experience'},{key:'openJobs',label:'Open jobs'}],
  RECRUITER: [{key:'company',label:'Company'},{key:'designation',label:'Designation'},{key:'hiringFor',label:'Hiring for'},{key:'experience',label:'Experience'},{key:'openJobs',label:'Open jobs'}],
  CEO: [{key:'company',label:'Company'},{key:'companyWebsite',label:'Company website'},{key:'industry',label:'Industry'},{key:'companySize',label:'Company size'},{key:'position',label:'Position'},{key:'experience',label:'Experience'},{key:'foundedYear',label:'Founded year'}],
  FOUNDER: [{key:'company',label:'Company'},{key:'companyWebsite',label:'Company website'},{key:'industry',label:'Industry'},{key:'companySize',label:'Company size'},{key:'position',label:'Position'},{key:'experience',label:'Experience'},{key:'foundedYear',label:'Founded year'}],
  PROFESSIONAL: [],
};

export type ProfilePayload = Omit<Profile, 'id' | 'firstName' | 'lastName' | 'website' | 'githubUrl' | 'linkedinUrl' | 'portfolioUrl'>;
