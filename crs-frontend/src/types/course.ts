export interface Course {
  id: number;
  code: string;
  name: string;
  credits: number;
  description?: string;
}

export interface PagedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

export interface CourseFormValues {
  tenMonHoc: string;
  soTinChi: string;
  soChoToiDa: string;
}

export const emptyCourseForm: CourseFormValues = {
  tenMonHoc: '',
  soTinChi: '',
  soChoToiDa: '',
};